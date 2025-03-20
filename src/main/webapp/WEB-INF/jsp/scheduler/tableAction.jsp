<script>
var table = $('#tableData').DataTable({
    order: [[0, 'desc']],
    responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
    "pagingType": "first_last_numbers",
      "lengthMenu": [[100, 200,-1], [100, 200, "All"]],
      dom: 'flrip',
} );

$('#tableData tr').click(function(){
      if($(this).attr("id") != null) {

          $(this).css('background-color','gray');
          $("#rowSelectorId").val($(this).attr("id"));
      }
});

table.on('dblclick', 'tr', function() {
var selectedData = table.row( this ).data();
    fire_ajax_submit("/schedule/"+selectedData[2]);
});

</script>