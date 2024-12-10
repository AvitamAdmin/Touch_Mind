<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <form method="POST" enctype="multipart/form-data" action="/handleTicketFinder" class="handle-upload" modelAttribute="uploadForm" >
          <br/>

          <div class="button-wrap">
              <label class="button" for="apiUrl">API URL(Optional):&nbsp; </label><input id="apiUrl" type="text" name="apiUrl">
              <br><input type="submit" value="Start" />
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