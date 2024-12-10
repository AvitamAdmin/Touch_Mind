<script>

var table = $('#tableData').DataTable({
    ordering: false,
    processing: true,
    responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
    "pagingType": "first_last_numbers",
      "lengthMenu": [[100, 200,-1], [100, 200, "All"]],
      dom: 'flrip',
      buttons: [
                  {
                     extend:    'excelHtml5',
                     title: '',
                     text: '<button class="btn btn-primary btn-icon" type="button">Extract</button>',
                     className: "cheil-excel-button",
                     filename: function(){
                         var d = new Date();

                         var n = d.getTime();
                         return 'Zero-in '+$("#navBreadcrumb").text().split(" > ")[2] + ' ' + d.toLocaleDateString('en-GB').split('/').reverse().join('') + '-' + n;
                     },
                     titleAttr: 'Extract',
                     header: 'true'
                 },
                ]
});

$('#tableData tr').click(function(){
      if($(this).attr("id") != null) {

          $(this).css('background-color','gray');
          $("#rowSelectorId").val($(this).attr("id"));
      }
});

$(document).ready(function () {
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

</script>