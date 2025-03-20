(function($) {
    $(document).ready(function() {

        let loc = window.location;
        let pathName = loc.pathname.substring(loc.pathname.lastIndexOf('/') + 1);

        $('#myNavbar ul li, #myNavbar ul li a').removeClass('active');
        $('.' + pathName + ',.' + pathName + ' a').addClass('active');

        $('.side-menu ul li').removeClass('active');
        $('.side-menu .' + pathName).addClass('active');

        $('.navbar-nav li a').on('click', function(e) {
            $('header .navbar-nav li, header .navbar-nav li a, .side-menu li').removeClass('active');
            $(this).parent('li').addClass('active');
            $(this).addClass('active');
        });

        $('.side-menu li a').on('click', function(e) {
            $('header .navbar-nav li, header .navbar-nav li a, .side-menu li').removeClass('active');
            $(this).parent('li').addClass('active');
        });

        $('.pager-search input[type=text]').focusin(
            function() {
                $('.pager-search svg').hide();
            }).focusout(
            function() {
                $('.pager-search svg').show();
            });

    });

    $(window).on('load', function() {
        $('#tableData').wrap('<div class="table-data-wrapper"></div>');
    });

})(jQuery);

function handleOperationForm(elm) {
    $('#checkType').val(elm.value);
    let action = "/toolkit/handleOperation".concat(elm.value.replace(/\s+/g, ''));
    $("#operationForm").attr("action", action);
    ajaxformSubmit("operationForm");
}

function ajaxformSubmit(id) {
    $("#editForm :input").removeAttr("disabled");
    $('#actionMessage').hide();
    var frm = $("#" + id);
    var url = frm.attr('action');
    frm.submit(function(e) {
        $("body").addClass("loading");
        e.preventDefault();
        e.stopImmediatePropagation();
        var formData = new FormData(this);
        $.ajax({
            type: frm.attr('method'),
            url: frm.attr('action'),
            data: formData,
            cache: false,
            contentType: false,
            processData: false,
            success: function(data) {
                $("body").removeClass("loading");
                console.log(data.redirect);
                if (data.redirect) {
                    // data.redirect contains the string URL to redirect to
                    window.location.href = data.redirect;
                } else {
                    // data.form contains the HTML for the replacement form
                    $('#appContent').html(data);
                    $("#navBreadcrumb").text(url.replaceAll('/', " > "));
                    $('.modal-backdrop').remove();
                    if (url.includes('edit')) {
                        $('#actionMessage').text('Data Modified/Added Successfully');
                        $('#actionMessage').show();
                    }
                    else if (url.includes('delete')) {
                        $('#actionMessage').text('Data Deleted Successfully');
                        $('#actionMessage').show();
                    }
                    else if (url.includes('upload')){
                        $('#actionMessage').text('Data uploaded successfully');
                        $('#actionMessage').show();
                    }
                }
            },
            error: function(data) {
                $("body").removeClass("loading");
                console.log(JSON.stringify(data));
                console.log(data.status);
            },
        });
    });
}

function submitOperationForm(url) {
    $('#actionMessage').hide();
    $("body").addClass("loading");
    $.ajax({
        type: "POST",
        url: url,
        timeout: 600000,
        success: function(data) {
            $("body").removeClass("loading");
            if (data.redirect) {
                // data.redirect contains the string URL to redirect to
                window.location.href = data.redirect;
            } else {
                // data.form contains the HTML for the replacement form
                $('#appContent').html(data);
            }
        },
        error: function(e) {
            $("body").removeClass("loading");
            $('#appContent').html(e);
        }
    });
}

function submitFormById(id) {
    $('#actionMessage').hide();
    $("body").addClass("loading");
    var form = $(id);
    //form.preventDefault(); // prevent actual form submit
    var url = form.attr('action'); //get submit url [replace url here if desired]
    $.ajax({
        type: "POST",
        url: url,
        data: form.serialize(), // serializes form input
        timeout: 600000,
        success: function(data) {
            $("body").removeClass("loading");
            console.log(data.redirect);
            if (data.redirect) {
                // data.redirect contains the string URL to redirect to
                window.location.href = data.redirect;
            } else {
                // data.form contains the HTML for the replacement form
                $('#appContent').html(data);
                $("#navBreadcrumb").text(url.replaceAll('/', " > "));
                $('.modal-backdrop').remove();
                if (url.includes('edit')) {
                    $('#actionMessage').text('Data Modified/Added Successfully');
                    $('#actionMessage').show()
                }
                else if (url.includes('delete')) {
                    $('#actionMessage').text('Data Deleted Successfully');
                    $('#actionMessage').show();
                }
                else if (url.includes('upload')){
                    $('#actionMessage').text('Data Uploaded Successfully');
                    $('#actionMessage').show();
                }
            }
        },
        error: function(e) {
            $("body").removeClass("loading");
            $('#appContent').html(e);
            console.log(e.status);
        }
    });
}

function fire_ajax_submit(url, ignoreBreadCrumb) {
    $('#actionMessage').hide();
    $("body").addClass("loading");
    $.ajax({
        type: "GET",
        url: url,
        timeout: 600000,
        success: function(data) {
        console.log(data.redirect);
            $("body").removeClass("loading");
            if (url.includes('toolkit')) {
                $('#sideMenu').hide();
                $('#toolkitSideMenu').show();
            } else {
                $('#sideMenu').show();
                $('#toolkitSideMenu').hide();
            }
            $('#appContent').html(data);
            $('.modal-backdrop').remove();
            if (url.includes('delete')) {
                $('#actionMessage').text('Data Deleted Successfully');
                $('#actionMessage').show();
            }
            if (url.includes('edits')) {
                $("#navBreadcrumb").text(" > Finders > Find Resolution");
            }
            else if (ignoreBreadCrumb != 'true') {
                $("#navBreadcrumb").text(url.replaceAll('/', " > "));
            }
            else {
                $("#navBreadcrumb").text(" > admin > scheduler");
            }
            if ($('#action_error').length) {
                console.log($("#action_error").text());
            }
            $('#tableData').wrap('<div class="table-data-wrapper"></div>');
        },
        error: function(e) {
            $("body").removeClass("loading");
            $('#appContent').html(e);
            console.log(e.status);
        }
    });
}

function gerParamsForDataSource(url, index) {
    $("body").addClass("loading");
    $.ajax({
        type: "GET",
        url: url + event.target.value,
        timeout: 600000,
        success: function(data) {
            $("body").removeClass("loading");
            var x = Number(index.substring(index.indexOf('[') + 1, index.indexOf(']')));
            $('#select_param_' + x).html(data);
        },
        error: function(e) {
            $("body").removeClass("loading");
            $('#select_param_').html(e);
        }
    });
}