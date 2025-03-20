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
             text: '<button class="btn btn-primary btn-icon" type="button">Create</button>',
             titleAttr: "Create",
             action: function ( e, dt, node, config ) {
                 fire_ajax_submit("/workflow/process");
             }
         }
    ]
} );

$('#tableData tr').click(function(){
      if($(this).attr("id") != null) {
      $(this).toggleClass('selected');
              var selectedIds = table.rows('.selected').data().pluck(0).toArray().toString();
              console.log(selectedIds);
                        $("#rowSelectorId").val(selectedIds);
                        var length=table.rows('.selected').data().length;
                        if(length==1)
                        {
                          $('#editBtn').removeAttr('disabled');
                          $('#deleteBtn').removeAttr('disabled');
                        }
                        if(length>1)
                          {
                            $('#editBtn').attr('disabled', 'disabled');
                            $('#deleteBtn').removeAttr('disabled');
                          }

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