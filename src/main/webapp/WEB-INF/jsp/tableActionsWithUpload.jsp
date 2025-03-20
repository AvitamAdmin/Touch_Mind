<c:set var="exportUrl" value="${baseUrl}/export"></c:set>
<script>
var table = $('#tableData').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
    "pagingType": "first_last_numbers",
      "lengthMenu": [[100, 200,-1], [100, 200, "All"]],
      dom: 'flripBt',
    buttons: [
         {
             text: '<button class="btn btn-primary btn-icon" type="button">Add</button>',
             titleAttr: "Add",
             action: function ( e, dt, node, config ) {
                 fire_ajax_submit("/"+$("#navBreadcrumb").text().split(" > ")[1]+"/"+$("#navBreadcrumb").text().split(" > ")[2]+"/add");
             }
         },
         {
              text: '<button class="btn btn-primary btn-icon" id="editBtn" type="button" disabled="disabled">Edit</button>',
             titleAttr: "Edit",
              action: function ( e, dt, node, config ) {
                  var value = $("#rowSelectorId").val();
                if(value) {
                    if(value.includes(",")){
                    $(".modal-backdrop").removeClass('modal-backdrop fade show');
                      $("#myModalEdit").modal('show');
                      $.ajax({
                                       type: 'GET',
                                       url: "/"+$("#navBreadcrumb").text().split(" > ")[1]+"/"+$("#navBreadcrumb").text().split(" > ")[2]+"/edits?id="+$("#rowSelectorId").val(),
                                       datatype: "json",
                                       success: function(data){
                                          $("#editModelContent").html(data);
                                       },
                                       error:function(e){
                                           console.log(e.statusText);
                                       }
                                   });
                    }else{
                      fire_ajax_submit("/"+$("#navBreadcrumb").text().split(" > ")[1]+"/"+$("#navBreadcrumb").text().split(" > ")[2]+"/edit?id="+$("#rowSelectorId").val());
                    }
                    } else {
                      alert('Please select the row you wanted to edit! ');
                    }
              }
          },
          {
               text: '<button class="btn btn-primary btn-icon" id="deleteBtn" type="button" disabled="disabled">Delete</button>',
                titleAttr: "Delete",
               action: function ( e, dt, node, config ) {
                    if( $("#rowSelectorId").val() ) {
                        fire_ajax_submit("/"+$("#navBreadcrumb").text().split(" > ")[1]+"/"+$("#navBreadcrumb").text().split(" > ")[2]+"/delete?id="+$("#rowSelectorId").val());
                    } else {
                        alert('Please select the row you wanted to delete! ');
                    }
               }
           },
        {
            extend:    'excelHtml5',
            title: '',
            text: '<button class="btn btn-primary btn-icon" type="button">Excel</button>',
            filename: function(){
               var d = new Date();

               var n = d.getTime();
               return 'Zero-in '+$("#navBreadcrumb").text().split(" > ")[2] + 'reports ' + d.toLocaleDateString('en-GB').split('/').reverse().join('') + '-' + n;
           },
            titleAttr: 'Excel',
            header: 'true'
        },
        {
                    text: '<button class="btn btn-primary btn-icon" id="templateBtn" type="button">Template</button>',
                    titleAttr: 'Template',
                },
        {
           text: '<button class="btn btn-primary btn-icon" id="uploadBtn" type="button">Upload</button>',
            titleAttr: "upload"
       }
    ]
} );

$("#tableData").on('click','tr',function(e) {
//$('#tableData tr').click(function(){
      if($(this).attr("id") != null) {
      $(this).toggleClass('selected');
              var selectedIds = table.rows('.selected').data().pluck(0).toArray().toString();
              console.log(selectedIds);
                        $("#rowSelectorId").val(selectedIds);
                        var length=table.rows('.selected').data().length;
                        if(length<=5)
                        {
                          $('#editBtn').removeAttr('disabled');
                          $('#deleteBtn').removeAttr('disabled');
                        }
                        if(length>1)
                          {
                            //$('#editBtn').attr('disabled', 'disabled');
                            $('#deleteBtn').removeAttr('disabled');
                          }

      }
});

$(document).ready(function () {
$("#uploadBtn").click(function(){
       $("#myModalUpload").modal('show');
   });
    // Setup - add a text input to each footer cell
    $('#tableData thead th').each(function () {
        var title = $(this).text();
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
    });
                table.columns().eq(0).each(function(colIdx) {
                            $('input', table.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                table
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                            });
                            });

                $('input').off('keyup keydown keypress');
});

table.on('dblclick', 'tr', function() {
var selectedData = table.row( this ).data();
    fire_ajax_submit("/"+$("#navBreadcrumb").text().split(" > ")[1]+"/"+$("#navBreadcrumb").text().split(" > ")[2]+"/edit?id="+selectedData[0]);
});

$("#templateBtn").click(function(){
           $.ajax({
               type: 'GET',
               url: "${exportUrl}",
               datatype: "json",
               success: function(url){
                    window.open(url, "_blank");
               },
               error:function(e){
                   console.log(e.statusText);
               }
           });
       });

</script>