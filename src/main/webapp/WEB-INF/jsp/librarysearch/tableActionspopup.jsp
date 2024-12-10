<script>
var tableDatalibpopup = $('#tableDatalibpopup').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'flrBt',
    buttons: [
         {
              text: '<button class="btn btn-primary btn-icon" id="addBtnLib" type="button">Add</button>',
              titleAttr: "Add",
              action: function ( e, dt, node, config ) {
                if( $("#rowSelectorIdAddSubLibrary").val() ) {

                $.ajax({
                  type: 'GET',
                  url: "/admin/library/addLib?libId="+$("#rowSelectorIdAddSubLibrary").val(),
                  datatype: "json",
                  success: function(data){
                  var tableData1 = $('#tableData1').DataTable();
                    var selectedIds = tableData1.rows().data().pluck(0).toArray().toString();
                   data.forEach( function(model) {
                   if(!selectedIds.split(',').includes(model.id)){
                        tableData1.row.add([model.id, model.description, model.type, model.subsidiaries]).node().id=model.id;
                        tableData1.draw();
                    }
                    });
                   var selectedIds = tableData1.rows().data().pluck(0).toArray().toString();
                    $("#rowSelectorIdSubLibrary").val(selectedIds);
                    $("#myLibraryModal").modal('hide');
                    var tableData1 = $('#tableData1').DataTable();
                    var selectedIds = tableData1.rows().data().pluck(0).toArray().toString();
                          selectedIds.split(',').forEach( function(selectedVal) {
                              tableDatalibpopup
                                      .rows('#'+selectedVal).remove().draw()
                          });
                  },
                  error:function(e){
                      console.log(e.statusText);
                  }
              });
                } else {
                  alert('Please select the row you wanted to add! ');
                }
              }
          },
    ]
} );

//$('#tableDatalibpopup tr').click(function(){
$("#tableDatalibpopup").on('click','tr',function(e) {
      if($(this).attr("id") != null) {
      $(this).toggleClass('selected');
              var selectedIds = tableDatalibpopup.rows('.selected').data().pluck(0).toArray().toString();
              console.log(selectedIds);
                        $("#rowSelectorIdAddSubLibrary").val(selectedIds);


      }
});

$(document).ready(function () {
    // Setup - add a text input to each footer cell
    $('#tableDatalibpopup thead th').each(function () {
        var title = $(this).text();
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
    });
                tableDatalibpopup.columns().eq(0).each(function(colIdx) {
                            $('input', tableDatalibpopup.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                tableDatalibpopup
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                            });
                            });

                $('input').off('keyup keydown keypress');
});

var tableData1 = $('#tableData1').DataTable();
var selectedIds = tableData1.rows().data().pluck(0).toArray().toString();
      selectedIds.split(',').forEach( function(selectedVal) {
          tableDatalibpopup
                  .rows('#'+selectedVal).remove().draw()
      });

</script>