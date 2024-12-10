<div>
<h3 style="margin-top:30px;text-align:center"> Welcome to Samsung test automation </h3>
</div>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <form method="POST" enctype="multipart/form-data" action="/handleTariff" class="handle-upload" modelAttribute="tariffForm" id="tariffForm">
          <br/>
          <label class="form-control">
            <input type="checkbox" name="checkedValue" value="tariffCheck"/>
            Tariff Anonymous User SIM Price
          </label>
          <label class="form-control">
            <input type="checkbox" name="checkedValue"  value="AllTests" />
            All Tests
          </label>
          </br>
          <div class="button-wrap">
              <label class="button" for="upload">Upload The Test Data:</label>
              <input id="upload" type="file" name="files[]" multiple accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
              <input type="submit" value="Start" onClick="ajaxformSubmit('tariffForm');"/>
            </div>
          </div>

      </form>
    </div>
  </div>
  <c:if test="${not empty message}">
      <div class="alert alert-danger" role="alert">
          <spring:message code="${message}" /> : <a style="color:blue;" href="${contextPath}/reports/${reportFile}" target="_blank">Report</a>
      </div>
  </c:if>

  <!--div>
    <ul>
      <c:forEach var="file" items="${files}">
        <li>
                <a th:href="${file}" th:text="${file}" />
            </li>
          </c:forEach>
    </ul>
  </div-->
</div>