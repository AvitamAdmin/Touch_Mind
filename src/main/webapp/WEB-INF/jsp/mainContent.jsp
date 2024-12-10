<%@ include file="include.jsp" %>
<div>
<h3 style="margin-top:30px;text-align:center"> Welcome to Samsung test automation </h3>
</div>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <form method="POST" enctype="multipart/form-data" action="/handleUpload" class="handle-upload" modelAttribute="uploadForm" id="uploadForm">
          <br/>
          <label class="form-control" style="font-size:12px;">
            <input type="checkbox" name="checkedValue" value="anonymousCheckoutWithSameAddress"/>
            Test_01_CheckoutSameShippingBillingAddress
          </label>

          <label class="form-control"  style="font-size:12px;">
            <input type="checkbox" name="checkedValue" value="anonymousCheckoutWithDifferentAddress" />
            Test_02_CheckoutDifferentShippingBillingAddress
          </label>

          <label class="form-control"  style="font-size:12px;">
            <input  type="checkbox" name="checkedValue" value="anonymousPaymentWithCreditCard"/>
             Test_03_GuestUserBuyS22WithCCPaymentMethod
          </label>
          
           <label class="form-control" style="font-size:10px;">
            <input  type="checkbox" name="checkedValue" value="anonymousPaymentWithPaypal"/>
             Test_04_GuestUserBuyS22WithPaypalPaymentMethod
          </label>
          
           <label class="form-control" style="font-size:10px;">
            <input  type="checkbox" name="checkedValue" value="anonymousPaymentWithSamsungPay"/>
             Test_05_GuestUserBuyS22WithSamsungPayPaymentMethod
          </label>

          <label class="form-control" style="font-size:12px;">
            <input type="checkbox" name="checkedValue"  value="AllTests" />
            All Tests
          </label>
</br>
          <div class="button-wrap">
              <label class="button" for="upload">Upload The Test Data:</label>
              <input id="upload" type="file" name="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"/>
              <input type="submit" value="Start" onClick="ajaxformSubmit('uploadForm');"/>
            </div>
          </div>

      </form>
    </div>
  </div>

  <c:if test="${not empty message}">
      <div class="alert alert-danger" role="alert">
          <spring:message code="${message}" /><c:if test="${not empty reportFile}"> : <a style="color:blue;" href="${contextPath}/reports/${reportFile}" target="_blank">Report</a></c:if>
      </div>
  </c:if>

</div>