<script>
var reportId = $('#reportId').text();
var table = $('#tableData').DataTable({
    ordering: false,
    processing: true,
    serverSide: true,
    responsive: true,
    ajax: '/toolkit/ajax/'+reportId,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
    "pagingType": "first_last_numbers",
     "lengthMenu": [[100, 200,-1], [100, 200, "All"]],
     dom: 'flripBt',
          buttons: [
              {
                attr:  {
                        id: 'import'
                },
                text: '<button class="btn btn-primary btn-icon" type="button">Shortcuts</button>',
                titleAttr: 'Shortcuts'
            },
            {
                attr:  {
                        id: 'modalBtn'
                },
                text: '<button class="btn btn-primary btn-icon" type="button">Import</button>',
                titleAttr: 'Import SKUs'
            },
            {
                attr:  {
                        id: 'uploadBtn'
                },
                text: '<button class="btn btn-primary btn-icon" type="button">Upload</button>',
                titleAttr: 'Upload'
            },
            {
                id:"refresh",
                text: '<button class="btn btn-primary btn-icon" id="refreshButton" type="button">Refresh</button>',
                titleAttr: 'Refresh'
            },
            {
                id:"save",
                text: '<button class="btn btn-primary btn-icon" type="button">Save</button>',
                titleAttr: 'Save'
            },
            {
               extend:    'excelHtml5',
               title: '',
               text: '<button class="btn btn-primary btn-icon" type="button">Extract</button>',
               className: "cheil-excel-button",
               filename: function(){
                   var d = new Date();

                   var n = d.getTime();
                   return 'Zero-in '+$("#navBreadcrumb").text().split(" > ")[2] + ' ' + d.toLocaleDateString('en-GB').split('/').reverse().join('') + '-' + n;
               },
               titleAttr: 'Extract',
               header: 'true'
           },
           {
               extend: 'copy',
               title: '',
               text: '<button class="btn btn-primary btn-icon" type="button">Copy</button',
               className: 'btn btn-primary btn-primary-spacing',
               header: false,
               exportOptions: {
                 columns: function ( idx, data, node ) {
                   return table.column( 'SKU:name' )
                 }
               }
           }
          ]
} );

$('#tableData tr').click(function(){
      if($(this).attr("id") != null) {

          $(this).css('background-color','gray');
          $("#rowSelectorId").val($(this).attr("id"));
      }
});

$("#import").click(function(){
   $("#myModal").modal('show');
   });
   $("#modalBtn").click(function(){
    $("#myModal2").modal('show');
   });
   $("#uploadBtn").click(function(){
       $("#myModal3").modal('show');
   });
   $("#cheil-row").hide();

   $(document).ready(function () {
       // Setup - add a text input to each footer cell
       $('#tableData thead th').each(function () {
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