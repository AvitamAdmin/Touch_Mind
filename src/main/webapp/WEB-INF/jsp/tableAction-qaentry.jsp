<c:set var="dom" value="flrip"></c:set>
<c:if test="${enableBtn == 'true'}">
    <c:set var="dom" value="flripBt"></c:set>
</c:if>
<c:choose>
    <c:when test="${not empty subsidiary}">
        <c:set var="url" value="/qa/results/${subsidiary}"></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="url" value="/qa/results?dashboard=${dashboard}"></c:set>
    </c:otherwise>
</c:choose>
<c:set var="selectedResults" value=""></c:set>
<script>
var table = $('#tableData').DataTable({
    ordering: false,
            responsive: true,
    language: {
        searchPlaceholder: "Search",
        search: "",
    },
    "pagingType": "first_last_numbers",
    "lengthMenu": [[100, 200,-1], [100, 200, "All"]],
    dom: '${dom}',
    serverSide: true,
    processing: true,
    paging: true,
    searching: { "regex": true },
    ajax: {
        url: '${url}',
        dataSrc:"content",
    },
    columnDefs: [{
        "defaultContent": "",
        "targets": "_all"
      }],
    columns : [
        { "data": ""},
        { "data": "subsidiary" },
        { "data": "site" },
        { "data": "locatorGroupIdentifier",
          "render":function(currentData,type,row){
            return '<a target="_blank" style="color:blue;" href="'+row.reportFilePath+'" >'+row.locatorGroupIdentifier+'</a>';
          }},
        { "data": "testName" },
        { "data": "sku" },
        { "data": "resultStatus",
            "render":function(currentData,type,row) {
                if(row.resultStatus==1) return "Passed";
                if(row.resultStatus==2) return "Failed";
                if(row.resultStatus==3) return "Partial Passed";
                return row.resultStatus;
            }
         },
        { "data": "failedSkusError",
          "render":function(currentData,type,row){
          if(row.failedSkusError) return JSON.stringify(Object.values(row.failedSkusError));
          return '';
          }},
        { "data": "user" },
        { "data": "creationTime" },
      ],
    buttons: [
        {
            extend:    'excelHtml5',
            title: '',
            text: '<button class="btn btn-primary btn-icon" type="button">Excel</button>',
            filename: function(){
               var d = new Date();

               var n = d.getTime();
               return 'Zero-in '+$("#navBreadcrumb").text().split(" > ")[2] + 'reports ' + d.toLocaleDateString('en-GB').split('/').reverse().join('') + '-' + n;
           },
            titleAttr: 'Excel',
            header: 'true'
        },
       {
            text: '<button class="btn btn-primary btn-icon" id="deleteBtn" type="button" disabled="disabled">Delete</button>',
             titleAttr: "Delete",
            action: function ( e, dt, node, config ) {
                 if( $("#rowSelectorId").val() ) {
                     fire_ajax_submit("/"+$("#navBreadcrumb").text().split(" > ")[1]+"/"+$("#navBreadcrumb").text().split(" > ")[2]+"/delete?id="+$("#rowSelectorId").val());
                 } else {
                     alert('Please select the row you wanted to delete! ');
                 }
            }
        },
        {
           text: '<button class="btn btn-primary btn-icon" id="import" type="button">Shortcuts</button>',
           titleAttr: "Shortcuts"
       },
       {
            text: '<button class="btn btn-primary btn-icon" id="emailBtn" type="button" disabled="disabled">Email</button>',
            titleAttr: "Email"
        }
    ]
} );
      var selectedIds = [];
      $(document).on("click","#tableData tr",function(e){
      e.preventDefault();
      e.stopPropagation();
      var selId = $(this).attr("id");
      if(selId != null) {
      $(this).toggleClass('selected');
      if ($(this).hasClass('selected')) {
              selectedIds.push(selId);
              console.log(selectedIds);
                        $("#rowSelectorId").val(selectedIds);
                        var length=selectedIds.length;
                        if(length>0)
                        {
                          $('#deleteBtnMedia').removeAttr('disabled');
                          $('#emailBtn').removeAttr('disabled');
                        }
                        if(length<=5)
                        {
                          $('#editBtn').removeAttr('disabled');
                          $('#deleteBtn').removeAttr('disabled');
                        }
                        if(length>1)
                          {
                            //$('#editBtn').attr('disabled', 'disabled');
                            $('#deleteBtn').removeAttr('disabled');
                          }
                          $('#selectedResults').val(selectedIds);
      }else{
        var index = selectedIds.indexOf(selId);
          if (index > -1) {
            selectedIds.splice(index, 1);
          }
      }
      }
});

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
     $("#import").click(function(){
     $("#myModal").modal('show');
     });
     $("#emailBtn").click(function(){
     $("#emailModal").modal('show');
     });


     $("#mailBtn").click(function(e){
           e.preventDefault();
           e.stopPropagation();
           $("body").addClass("loading");
           var form = $('#cronForm');
                $.ajax({
                    type: 'POST',
                    data: form.serialize(),
                    url: "/qa/sendMail",
                    datatype: "json",
                    success: function(data){
                    $("body").removeClass("loading");
                    var qaUrl = '/qa/results';
                    if(data == 'Success'){
                        var htmlData = '<p style="font-weight:bold;">Mail sent Successfully</p>'
                        $('#sendMailPopup').html(htmlData);
                        setTimeout( "fire_ajax_submit('/qa/results');",2000 );
                    }
                    else{
                       var htmlData = '<p style="font-weight:bold;">Mail sending failed</p>'
                       $('#sendMailPopup').html(htmlData);
                       setTimeout( "fire_ajax_submit('/qa/results');",2000 );
                    }
                    },
                    error:function(e){
                    $("body").removeClass("loading");
                        console.log(e.statusText);
                    }
                });
            });
</script>

<div id="emailModal" class="modal fade" tabindex="-1">
               <div class="modal-dialog-full" style="margin-left:200px;margin-top:100px;">
                    <div class="modal-content" style="height:65%;">
                       <div class="modal-body" id="sendMailPopup">
                  <form:form method="POST" id="cronForm" enctype="multipart/form-data" action="/qa/sendMail" class="handle-upload" modelAttribute="cronForm" >
                     <input type="hidden" id="selectedResults" name="id" value="${selectedIds}">
                     <div class="row">
                       <div class="col-sm-3">
                           <select class="cheil-select" name="cronProfileId" id="cronProfileId" placeholder="Select Cron profile" required="required">
                           <span>Select Cron profile</span>
                              <c:forEach items="${cronProfiles}" var="child">
                                 <option value="${child.recordId}" >${child.identifier}</option>
                              </c:forEach>
                           </select>
                        </div>
                        <div class="col-sm-3">
                             <form:input path="emailSubject" class="inputbox-cheil" placeholder="Enter email subject" required="required" />
                             <span>Enter email subject</span>
                        </div>
                        <div class="col-sm-3">
                        <button class="btn btn-primary add-more" aria-controls="tableData" id="mailBtn" type="button">Send Mail</button>
                        </div>
                        <div class="col-sm-3">
                          <button class="btn btn-primary add-more" style="margin-left:-220px;" onclick="javascript:fire_ajax_submit('/qa/results')" aria-controls="tableData" type="button">Cancel</button>
                        </div>
                        </div>
                     </div>
                     </form:form>
                  </div>
            </div>
       </div>
  </div>