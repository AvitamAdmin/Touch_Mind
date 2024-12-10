<script>
var tableData3 = $('#tableData3').DataTable({
    ordering: false,
    responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'lrBt',
    buttons: [
            {
              text: '<button class="btn btn-primary btn-icon" id="addBtnAction" type="button">Add</button>',
             titleAttr: "Add",
              action: function ( e, dt, node, config ) {

                }
          },
            {
                  text: '<button class="btn btn-primary btn-icon" id="editBtnAction" disabled="disabled" type="button">Edit</button>',
                 titleAttr: "Edit",
                  action: function ( e, dt, node, config ) {
                        if( $("#rowSelectorAction").val() ) {
                          $('#action-input').show();
                          $('#text-only').hide();
                        } else {
                          alert('Please select the row you wanted to edit! ');
                        }
                    }
              },
              {
                text: '<button class="btn btn-primary btn-icon" id="up" type="button">Move Up</button>',
               titleAttr: "Move Up",
            },
            {
              text: '<button class="btn btn-primary btn-icon" id="down" type="button">Move Down</button>',
             titleAttr: "Move Down",
          },
         {
             text: '<button class="btn btn-primary btn-icon" id="removeBtnAction" disabled="disabled" type="button">Remove</button>',
             titleAttr: "Remove",
             action: function ( e, dt, node, config ) {
                                     if( $("#rowSelectorAction").val() ) {
                                       tableData3.rows('.selected').remove().draw();
                                       var selectedIds = tableData3.rows().data().pluck(1).toArray().toString();
                                        console.log(selectedIds);
                                        $("#rowSelectorIdAction").val(selectedIds);
                                        selectedIds.split(',').forEach( function(id) {
                                            $('#pinImg'+id).click(function(){
                                                $('#checkAction'+id).removeAttr('checked');
                                               $('#pinImg'+id).hide();
                                           });
                                        });
                                     } else {
                                       alert('Please select the row you wanted to edit! ');
                                     }
                                 }
         },
         {
                       text: '<button class="btn btn-primary btn-icon" id="pin" type="button">Pin</button>',
                      titleAttr: "Pin",
                       action: function ( e, dt, node, config ) {

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
                            return 'Zero-in Actions ' + d.toLocaleDateString('en-GB').split('/').reverse().join('') + '-' + n;
                        },
                        titleAttr: 'Extract',
                        header: 'true'
                    }
    ]
} );

$("#tableData3").on('click','tr',function(e) {
//$('#tableData3 tr').click(function(){

      if($(this).attr("id") != null) {

      $(this).toggleClass('selected');
              var selectedIds = tableData3.rows('.selected').data().pluck(1).toArray().toString();
              console.log(selectedIds);
              if($(this).hasClass('selected')){
                $('#checkAction'+$(this).attr("id")).attr('checked', 'checked');
              }else{
                $('#checkAction'+$(this).attr("id")).removeAttr('checked');
              }

            $("#rowSelectorAction").val(selectedIds);
            var length=tableData3.rows('.selected').data().length;
            if(length>0)
            {
              $('#removeBtnAction').removeAttr('disabled');
            }
            if(length==1)
            {
              $('#editBtnAction').removeAttr('disabled');
            }
            if(length>1)
              {
                $('#editBtnAction').attr('disabled', 'disabled');
              }

      }
});

$(document).ready(function () {

    // Setup - add a text input to each footer cell
    $('#tableData3 thead th').each(function () {
        var title = $(this).text();
        if(title){
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
        }
    });
                tableData3.columns().eq(0).each(function(colIdx) {
                            $('input', tableData3.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                if(colIdx>0){
                                tableData3
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                                        }
                            });
                            });
                $('input').off('keyup keydown keypress');
                 var selectedIds = tableData3.rows().data().pluck(1).toArray().toString();
                 console.log(selectedIds);
                 $("#rowSelectorIdAction").val(selectedIds);
                             selectedIds.split(',').forEach( function(id) {
                                 $('#pinImg'+id).click(function(){
                                    $('#pinImg'+id).hide();
                                });
                             });

            var timeout = null;
            $("[id*='remarks-']").on("input", function() {
                               var libId = $('#currentLibId').text();
                               var remarks = $(this).val();
                               var actionId = $(this).attr('id').split('-');
                               clearTimeout(timeout);

                                   timeout = setTimeout(function() {
                                       $.ajax({
                                          type: 'GET',
                                          url: "/admin/library/saveremarks?libId=" + libId + "&remarks="+ actionId[1] + "-" + remarks,
                                          datatype: "json",
                                          success: function(data){

                                          },
                                          error:function(e){
                                              console.log(e.statusText);
                                          }
                                      });
                                   }, 500);

              });

});

$('#pin,#up,#down').click(function(){
//$(document).on('click', '#up, #down, #pin', function() {
    button_choice = $(this).attr('id');
    $('.ch:checked').each(function() {

          row = $(this).closest("tr");
          checked_length = $('.ch:checked').length;

          if(button_choice == 'up') {
            //if(row.index() > 1) {
            row.insertBefore(row.prev())
            //}
          }
          if(button_choice == 'down') {
            for(i=0;i<checked_length;i++) { row.insertAfter(row.next()) }
          }
          if(button_choice == 'pin') {
          var rowCount = $('#tableData3 tr').length;
            for(i=0;i<rowCount-1;i++) { row.insertBefore(row.prev()) }
                var selectedIds = tableData3.rows('.selected').data().pluck(1).toArray().toString();
                selectedIds.split(',').forEach( function(id) {
                    $('#pinImg'+id).show();
                });
            }
});
});
</script>