<script>
var table1 = $('#tableData1').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'lrBt',
    buttons: [
         {
              text: '<button class="btn btn-primary btn-icon" id="addBtnLib" type="button">Add</button>',
              titleAttr: "Add",
              action: function ( e, dt, node, config ) {
              }
          },
         {
             text: '<button class="btn btn-primary btn-icon" id="deleteBtnLib" disabled="disabled" type="button">Remove</button>',
             titleAttr: "Remove",
             action: function ( e, dt, node, config ) {
                 if( $("#rowSelectorSubLibrary").val() ) {
                     table1.rows('.selected').remove().draw();
                     var selectedIds = table1.rows().data().pluck(0).toArray().toString();
                     $("#rowSelectorIdSubLibrary").val(selectedIds);
                   } else {
                     alert('Please select the row you wanted to remove! ');
                   }
             }
         },
         {
                        extend:    'excelHtml5',
                        title: '',
                        text: '<button class="btn btn-primary btn-icon" type="button">Extract</button>',
                        className: "cheil-excel-button",
                        filename: function(){
                            var d = new Date();
                            var n = d.getTime();
                            return 'Zero-in RelatedLibrary ' + d.toLocaleDateString('en-GB').split('/').reverse().join('') + '-' + n;
                        },
                        titleAttr: 'Extract',
                        header: 'true'
                    }
    ]
} );

$("#tableData1").on('click','tr',function(e) {
//$('#tableData1 tr').click(function(){
if($(this).attr("id") != null) {
      $(this).toggleClass('selected');
              var selectedIds = table1.rows('.selected').data().pluck(0).toArray().toString();
              console.log(selectedIds);
                        $("#rowSelectorSubLibrary").val(selectedIds);
                        var length=table1.rows('.selected').data().length;
                        if(length>0)
                        {
                          $('#deleteBtnLib').removeAttr('disabled');
                        }

      }
});


$(document).ready(function () {
    // Setup - add a text input to each footer cell
    $('#tableData1 thead th').each(function () {
        var title = $(this).text();
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
    });
                table1.columns().eq(0).each(function(colIdx) {
                            $('input', table1.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                table1
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                            });
                            });

                $('input').off('keyup keydown keypress');
                var selectedIds = table1.rows().data().pluck(0).toArray().toString();
                 $("#rowSelectorIdSubLibrary").val(selectedIds);

});


</script>