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
                    if( $("#rowSelectorAddAction").val() ) {
                       $.ajax({
                         type: 'GET',
                         url: "/admin/library/addAction?actionId="+$("#rowSelectorAddAction").val(),
                         datatype: "json",
                         success: function(data){
                            var tableData3 = $('#tableData3').DataTable();
                            var selectedIds = tableData3.rows().data().pluck(1).toArray().toString();
                                data.forEach( function(model) {
                                if(!selectedIds.split(',').includes(model.id)){
                                    tableData3.row.add(["<img id='pinImg" +model.id+"' src='${contextPath}/images/pin.png' style='display:none;width:20px;height:20px;margin-bottom:10px;margin-left:-8px;' /><br/><input class='ch' id='checkAction" + model.id+"' type='checkbox'>",model.id, model.system.shortDescription, model.description, model.systemPath, model.mediaIds, '', model.subsidiaries]).node().id=model.id;
                                    tableData3.draw();
                               }
                               });

                           var selectedIds = tableData3.rows().data().pluck(1).toArray().toString();
                            $("#rowSelectorIdAction").val(selectedIds);
                            $("#myActionModal").modal('hide');

                            var tableData3 = $('#tableData3').DataTable();
                            var selectedIds = tableData3.rows().data().pluck(1).toArray().toString();
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
            $("#rowSelectorAddAction").val(selectedIds);

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
var tableData3 = $('#tableData3').DataTable();
                            var selectedIds = tableData3.rows().data().pluck(1).toArray().toString();
                          selectedIds.split(',').forEach( function(selectedVal) {
                              tableData3popup
                                      .rows('#'+selectedVal).remove().draw()
                          });

</script>