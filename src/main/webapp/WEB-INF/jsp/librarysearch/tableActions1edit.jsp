<script>
$( "#collapseMedia" ).accordion({
            collapsible: true,
            active: false
});
var tableData2media = $('#tableData2media').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'lrtp',

} );

$('#tableData2media tr').click(function(){
      if($(this).attr("id") != null) {
      $(this).toggleClass('selected');
              var selectedIds = tableData3edit.rows('.selected').data().pluck(0).toArray().toString();
                        $("#rowSelectorId").val(selectedIds);

      }
});

$(document).ready(function () {
    // Setup - add a text input to each footer cell
    $('#tableData2media thead th').each(function () {
        var title = $(this).text();
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
    });
                tableData2media.columns().eq(0).each(function(colIdx) {
                            $('input', tableData2media.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                tableData2media
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                            });
                            });

                $('input').off('keyup keydown keypress');
});

</script>