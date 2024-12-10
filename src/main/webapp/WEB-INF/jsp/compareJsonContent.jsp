<div>
<h3 style="margin-top:30px;text-align:center"> Welcome to Samsung test automation </h3>
</div>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <form method="POST" id="compareJsonForm" enctype="multipart/form-data" action="/handleCompareJson" class="handle-upload" modelAttribute="uploadForm" >
          <br/>

          <div class="button-wrap">
              <label class="button" for="upload">Upload valid Json file for Comparison:</label><br>
              <br><label class="button" for="file">File One:&nbsp; </label><input id="upload" type="file" name="file" accept="application/JSON">
              <br><br><label class="button" for="apiUrl">API URL(Optional):&nbsp; </label><input id="apiUrl" type="text" name="apiUrl">
              <br/><input type="submit" value="Start" onClick="ajaxformSubmit('compareJsonForm');"/>
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