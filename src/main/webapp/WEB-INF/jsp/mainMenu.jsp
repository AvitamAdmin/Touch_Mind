<nav class="main-menu navbar navbar-inverse">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>                        
      </button>

    </div>
     <form id="logoutForm" method="POST" action="${contextPath}/logout">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <div class="collapse navbar-collapse" id="myNavbar">
      <ul class="nav navbar-nav">
        <li class="active home"><a class="active" href="/home">Home</a></li>
        <li class="reports"><a href="javascript:fire_ajax_submit('/reports')">Healthcheck</a></li>
        <li class="reports"><a href="javascript:fire_ajax_submit('/toolkit')">Toolkits</a></li>
          <li class="reports"><a href="javascript:fire_ajax_submit('/admin/users')">Admin</a></li>
        <li class="reports">
          <span class="loginUser">
            <img width="20px" height="20px" src="${contextPath}/images/logout.jpeg"/>&nbsp;${pageContext.request.userPrincipal.name}
            <a class="logout" onclick="document.forms['logoutForm'].submit()">Logout</a>
          </span>
        </li>
      </ul>
      
    </div>
  </div>
</nav>