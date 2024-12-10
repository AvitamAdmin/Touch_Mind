<div class="main-content">

 <form method="POST" action="${contextPath}/login" class="form-signin">
         <div class="row">
                <h2 class="form-heading">Log in</h2>
        </div>
        <div class="form-group ${error != null ? 'has-error' : ''}">
            <span>${message}</span>
            <input name="username" type="text" class="form-control" placeholder="Username"
            <span>Username</span>
                   autofocus="true"/>
            </br>
            <input name="password" type="password" class="form-control" placeholder="Password"/>
            <span>Password</span>
            <span>${error}</span>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <button class="btn btn-lg btn-primary btn-block" type="submit">Log In</button>
            <h4 class="text-center"><a href="${contextPath}/register">Create an account</a></h4>

      </form>
    </div>
  </div>