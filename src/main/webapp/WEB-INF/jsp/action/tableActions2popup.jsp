<script>
var tableData3popup = $('#tableData3popup').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'flrBt',
    buttons: [
            {
              text: '<button class="btn btn-primary btn-icon" id="addBtnAction" type="button">Add</button>',
             titleAttr: "Add",
              action: function ( e, dt, node, config ) {
                    if( $("#rowSelectorRelatedAddAction").val() ) {
                       $.ajax({
                         type: 'GET',
                         url: "/admin/library/addAction?actionId="+$("#rowSelectorRelatedAddAction").val(),
                         datatype: "json",
                         success: function(data){
                            var tableDatarelated = $('#tableDatarelated').DataTable();
                            var selectedIds = tableDatarelated.rows().data().pluck(0).toArray().toString();
                                data.forEach( function(model) {
                                if(!selectedIds.split(',').includes(model.id)){
                                    tableDatarelated.row.add([model.id, model.description, model.system.shortDescription, model.systemPath, model.subsidiaries]).node().id=model.id;
                                    tableDatarelated.draw();
                               }
                               });

                           var selectedIds = tableDatarelated.rows().data().pluck(0).toArray().toString();
                            $("#rowSelectorIdRelatedAction").val(selectedIds);
                            $("#myRelatedActionModal").modal('hide');
                            var tableDatarelated = $('#tableDatarelated').DataTable();
                              var selectedIds = tableDatarelated.rows().data().pluck(0).toArray().toString();
                                  selectedIds.split(',').forEach( function(selectedVal) {
                                      tableData3popup
                                              .rows('#'+selectedVal).remove().draw()
                                  });
                         },
                         error:function(e){
                             console.log(e.statusText);
                         }
                     });
                    } else {
                      alert('Please select the row you wanted to add!');
                    }
                }
          },

    ]
} );

//$('#tableData3popup tr').click(function(){
$("#tableData3popup").on('click','tr',function(e) {
      if($(this).attr("id") != null) {

      $(this).toggleClass('selected');
              var selectedIds = tableData3popup.rows('.selected').data().pluck(0).toArray().toString();
            $("#rowSelectorRelatedAddAction").val(selectedIds);

      }
});

$(document).ready(function () {
    // Setup - add a text input to each footer cell
    $('#tableData3popup thead th').each(function () {
        var title = $(this).text();
        if(title){
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
        }
    });
                tableData3popup.columns().eq(0).each(function(colIdx) {
                            $('input', tableData3popup.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                tableData3popup
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                            });
                            });

                $('input').off('keyup keydown keypress');
});

                                  var tableDatarelated = $('#tableDatarelated').DataTable();
                                  var selectedIds = tableDatarelated.rows().data().pluck(0).toArray().toString();
                                      selectedIds.split(',').forEach( function(selectedVal) {
                                          tableData3popup
                                                  .rows('#'+selectedVal).remove().draw()
                                      });

</script>