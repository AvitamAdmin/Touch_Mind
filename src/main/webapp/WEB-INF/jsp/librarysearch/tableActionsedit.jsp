<script>
$( "#collapseSubLib" ).accordion({
    collapsible: true,
    active: false
});
var tableData1sub = $('#tableData1sub').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'lrtp',

} );

$('#tableData1sub tr').click(function(){
      if($(this).attr("id") != null) {
      $(this).toggleClass('selected');
              var selectedIds = tableData1sub.rows('.selected').data().pluck(0).toArray().toString();
              console.log(selectedIds);
                        $("#rowSelectorId").val(selectedIds);
                        var length=tableData1sub.rows('.selected').data().length;
                        if(length==1)
                        {
                          $('#editBtn').removeAttr('disabled');
                        }
                        if(length>1)
                          {
                            $('#editBtn').attr('disabled', 'disabled');
                          }

      }
});

$(document).ready(function () {
    // Setup - add a text input to each footer cell
    $('#tableData1sub thead th').each(function () {
        var title = $(this).text();
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
    });
                tableData1sub.columns().eq(0).each(function(colIdx) {
                            $('input', tableData1sub.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                tableData1sub
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                            });
                            });

                $('input').off('keyup keydown keypress');
});

</script>