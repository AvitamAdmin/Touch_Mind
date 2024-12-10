<%@ include file="security/loginHeader.jsp" %>
  <body>
  <div class="modal" id="loading">
  						<p>Otp generated</p>
  					</div>
  <br/>
  <br/>
  <br/>
  <br/>
    <div class="main-content">
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert">
               <p style="text-align:center">${message}</p>
           </div>
       </c:if>
      <form method="POST" action="${contextPath}/login" id="loginForm" class="form-signin">
      <br/>
      <div class="row">
              <div class="col-sm-4"></div>
              <div style="text-align:center;" class="col-sm-4">
                  <img style="width:150px;" src="${contextPath}/images/cheil.png"/>
              </div>
              <div class="col-sm-4"></div>
      </div>
      <br/><br/>
      <div class="row form-group ${error != null ? 'has-error' : ''}">
            <div class="col-sm-4"></div>
            <div class="col-sm-4">
                <input name="username" type="email" class="inputbox-cheil-small" id="email" placeholder="Email" required/>
                <span>Email Id</span>
            </div>
            <div class="col-sm-4"></div>
      </div>
      </br>
        <div class="row form-group ${error != null ? 'has-error' : ''}">
                <div class="col-sm-4"></div>
                <div class="col-sm-4">
                    <input name="password" type="password" class="inputbox-cheil-small" placeholder="Password" required/>
                    <span>Password</span>
                </div>
                <div class="col-sm-4"></div>
        </div>
        <br/>
        <c:choose>
        <c:when test="${otpEnabled=='true'}">
        <div class="row" id="otpSection">
                <div class="col-sm-4"></div>
                <div class="col-sm-4">
                    <input name="otp" class="inputbox-cheil-small" id="otp" placeholder="Enter OTP" required/>
                    <span>Enter OTP</span>
                </div>
                <div class="col-sm-4"><button class="btn-primary" style="background-color: #333333;margin-top:10px;" type="button" id="sendOtp">Generate OTP</button></div>
          </div>
          </br>
        <div class="row form-group ${error != null ? 'has-error' : ''}">
            <div class="col-sm-4"></div>
                <div class="col-sm-4 text-center">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-lg btn-primary btn-cheil" type="button" id="submitLogin">Log In</button>
                </div>
            <div class="col-sm-4">
            </div>
        </div>
        </br>
        </c:when>
        <c:otherwise>
            <div class="row form-group ${error != null ? 'has-error' : ''}">
                        <div class="col-sm-4"></div>
                            <div class="col-sm-4 text-center">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button class="btn btn-lg btn-primary btn-cheil" type="submit">Log In</button>
                            </div>
                        <div class="col-sm-4">
                        </div>
                    </div>
                    </br>
        </c:otherwise>
        </c:choose>

        <div style="text-align:center;color:red;font-weight:bold;" id="otpMessage"></div>
        </br>

         <div class="row">
             <div class="col-sm-4"></div>
             <div class="col-sm-4">
                 <h4 class="text-center cheil-text"><a href="${contextPath}/forgotpassword">Reset the password</a></h4>
             </div>
             <div class="col-sm-4"></div>
          </div>
          <br/>
          <div class="row">
               <div class="col-sm-4"></div>
               <div class="col-sm-4">
                   <h4 class="text-center">OR</h4>
               </div>
               <div class="col-sm-4"></div>
         </div>
         <br/>
         <div class="row">
            <div class="col-sm-4"></div>
            <div class="col-sm-4 text-center">
                <button class="btn btn-lg btn-primary btn-cheil" type="button" onclick="location.href='${contextPath}/register'">Register</button>
            </div>
            <div class="col-sm-4"></div>
         </div>
      </form>
    </div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

    <script>
    $("#sendOtp").click(function(){
    $("body").addClass("loading");
                 var emailId = $("#email").val();
                 $.ajax({
                     type: 'GET',
                     url: "/otplogin?email=" + emailId,
                     datatype: "json",
                     success: function(data){
                     $("body").removeClass("loading");
                     if(data == 'Success'){
                        $("#sendOtp").text("Resend OTP");
                        }else{
                        $("#otpMessage").text(data);
                        }
                     },
                     error:function(e){
                         console.log(e.statusText);
                     }
                 });
             });

      $("#submitLogin").click(function(){
      var form = $('#loginForm');
           $.ajax({
               type: 'POST',
               data: form.serialize(),
               url: "/otplogin",
               datatype: "json",
               success: function(data){
               if(data == 'Success'){
                   location.href='${contextPath}/home';
               }
               else{
                  $("#otpMessage").text(data);
               }
               },
               error:function(e){
                   console.log(e.statusText);
               }
           });
       });

    </script>
  </body>
  <%@ include file="footer.jsp" %>
</html>