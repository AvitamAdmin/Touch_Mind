<div>
<h3 style="margin-top:30px;text-align:center"> Welcome to Samsung test automation </h3>
</div>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <form method="POST" enctype="multipart/form-data" action="/handleCompareExcel" class="handle-upload" modelAttribute="uploadForm" id="compareExcelForm">
          <br/>

          <div class="button-wrap">
              <label class="button" for="upload">Upload 2 files for Comparision:</label><br>
              <br/><label class="button" for="file">File One:&nbsp; </label><br/><input id="upload" type="file" name="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
              <br/><br/><label class="button" for="file2">File Two:&nbsp; </label><br/><input id="upload2" type="file" name="file2" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
              <br/><br/><input type="submit" value="Start" onClick="ajaxformSubmit('compareExcelForm');"/>
            </div>
          </div>
          <c:if test="${not empty message}">
              <div class="alert alert-danger" role="alert">
                  <spring:message code="${message}" /> : <a style="color:blue;" href="${contextPath}/reports/${reportFile}" target="_blank">Report</a>
              </div>
          </c:if>
      </form>
    </div>
  </div>


</div>