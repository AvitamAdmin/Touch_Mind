<script>
var table = $('#tableData2').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'lrBt',
      buttons: [
                   {
                   text: '<button class="btn btn-primary btn-icon" id="deleteBtnMedia" disabled="disabled" type="button">Remove</button>',
                   titleAttr: "Remove",
                   action: function ( e, dt, node, config ) {
                       if( $("#rowSelectorId").val() ) {
                           table.rows('.selected').remove().draw();
                           var selectedIds = table.rows().data().pluck(0).toArray().toString();
                           $("#rowSelectorId").val(selectedIds);
                         } else {
                           alert('Please select the row you wanted to remove! ');
                         }
                   }
               },
          ]

} );
$('#tableData2 tr').click(function(){
      if($(this).attr("id") != null) {
      $(this).toggleClass('selected');
              var selectedIds = table.rows('.selected').data().pluck(0).toArray().toString();
              console.log(selectedIds);
                        $("#rowSelectorId").val(selectedIds);
                        var length=table.rows('.selected').data().length;
                        if(length>0)
                        {
                          $('#deleteBtnMedia').removeAttr('disabled');
                        }

      }
});

$(document).ready(function () {
    // Setup - add a text input to each footer cell
    $('#tableData2 thead th').each(function () {
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