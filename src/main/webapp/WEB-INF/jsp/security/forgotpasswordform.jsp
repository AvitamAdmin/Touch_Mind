<div class="main-content">
</br>
            </br>
 <form method="POST" action="${contextPath}/forgotpassword" class="form-signin">
         <div class="row" style="text-align:center;">
         <div class="col-sm-4"></div>
        <div class="col-sm-4">
            <h3 class="form-heading">Enter Email To Reset password</h3>
            </br>
            </br>
            <span>${message}</span>
            <input name="email" type="text" class="form-control" placeholder="Username/Email"/>
            <span>Username/Email</span>
            </br>
            </br>
            <button class="btn btn-lg btn-primary btn-cheil" type="submit">Send Link</button>
        </div>
        <div class="col-sm-4"></div>
        </div>
      </form>
      </br>
      <div class="col-sm-12" style="font-size:14px; text-align:center;">Back to <a href="${contextPath}/login">Sign in.</a></div>
    </div>
  </div>