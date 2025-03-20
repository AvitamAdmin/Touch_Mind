<script>
$( "#collapseAction" ).accordion({
    collapsible: true,
    active: true
});
var tableData3edit = $('#tableData3edit').DataTable({
    ordering: false,
            responsive: true,
            select: false,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'lrtp',

} );

$('#tableData3edit tr').click(function(){

      if($(this).attr("id") != null) {
      $(this).toggleClass('selected');


      }
});

$(document).ready(function () {
    // Setup - add a text input to each footer cell
    $('#tableData3edit thead th').each(function () {
        var title = $(this).text();
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
    });
                tableData3edit.columns().eq(0).each(function(colIdx) {
                            $('input', tableData3edit.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                tableData3edit
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                            });
                            });

                $('input').off('keyup keydown keypress');
});

</script>