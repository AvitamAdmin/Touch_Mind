<script>
var tableDatarelated = $('#tableDatarelated').DataTable({
    ordering: false,
    responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
      dom: 'lrBt',
    buttons: [
            {
              text: '<button class="btn btn-primary btn-icon" id="addBtnRelatedAction" type="button">Add</button>',
             titleAttr: "Add",
              action: function ( e, dt, node, config ) {

                }
          },

         {
             text: '<button class="btn btn-primary btn-icon" id="removeBtnAction" disabled="disabled" type="button">Remove</button>',
             titleAttr: "Remove",
             action: function ( e, dt, node, config ) {
                                     if( $("#rowSelectorAction").val() ) {
                                       tableDatarelated.rows('.selected').remove().draw();
                                       var selectedIds = tableDatarelated.rows().data().pluck(0).toArray().toString();
                                        $("#rowSelectorIdRelatedAction").val(selectedIds);
                                     } else {
                                       alert('Please select the row you wanted to edit! ');
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
                            return 'Zero-in Actions ' + d.toLocaleDateString('en-GB').split('/').reverse().join('') + '-' + n;
                        },
                        titleAttr: 'Extract',
                        header: 'true'
                    }
    ]
} );

$("#tableDatarelated").on('click','tr',function(e) {
//$('#tableData3 tr').click(function(){

      if($(this).attr("id") != null) {

      $(this).toggleClass('selected');
              var selectedIds = tableDatarelated.rows('.selected').data().pluck(0).toArray().toString();
              console.log(selectedIds);
              if($(this).hasClass('selected')){
                $('#checkAction'+$(this).attr("id")).attr('checked', 'checked');
              }else{
                $('#checkAction'+$(this).attr("id")).removeAttr('checked');
              }

            $("#rowSelectorAction").val(selectedIds);
            var length=tableDatarelated.rows('.selected').data().length;
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
    $('#tableDatarelated thead th').each(function () {
        var title = $(this).text();
        if(title){
        $(this).html('<p style="display:none;">'+title+'</p><input type="text" id=' +title+ ' placeholder="Search ' + title + '" />');
        }
    });
                tableDatarelated.columns().eq(0).each(function(colIdx) {
                            $('input', tableDatarelated.column(colIdx).header()).on('change', function() {
                                console.log(colIdx + '-' + this.value);
                                if(colIdx>0){
                                tableDatarelated
                                        .column(colIdx)
                                        .search(this.value)
                                        .draw();
                                        }
                            });
                            });
                $('input').off('keyup keydown keypress');
                 var selectedIds = tableDatarelated.rows().data().pluck(0).toArray().toString();
                 console.log(selectedIds);
                 $("#rowSelectorIdRelatedAction").val(selectedIds);


});
</script>