<div class="main-content">
</br>
</br>
  <div class="row" style="text-align:center;">
    <form method="POST" action="${contextPath}/resetpassword" class="form-signin">
      <br/>
            <input type="hidden" name="token" value="${token}"/>
            <h3 class="form-heading">Reset Password</h3>
                        </br>
                        </br>
            <div class="row form-group ${status.error ? 'has-error' : ''}">
                <div class="col-sm-4"></div>
                <div class="col-sm-4">
                    <input type="password" name="password" class="inputbox-cheil" placeholder="New Password"/>
                    <span>New Password</span>
                </div>
                <div class="col-sm-4"></div>
            </div>

            <div class="row form-group ${status.error ? 'has-error' : ''}">
                <div class="col-sm-4"></div>
                <div class="col-sm-4">
                    <input type="password" name="passwordConfirm" class="inputbox-cheil-long"
                            placeholder="Confirm the Password"/>
                            <span>Confirm the Password</span>
                </div>
                <div class="col-sm-4"></div>
            </div>

        <br/>
        <div class="row">
            <div class="col-sm-4"></div>
            <div class="col-sm-4" style="display:flex;justify-content: center;">
                <button class="btn btn-lg btn-primary btn-block btn-cheil" type="submit">Submit</button>
            </div>
            <div class="col-sm-4"></div>
        </div>
      </form>
      <c:if test="${not empty message}">
         <div class="alert alert-danger" role="alert" id="errorMessage">
             <spring:message code="${message}" />
         </div>
     </c:if>
    </div>
  </div>