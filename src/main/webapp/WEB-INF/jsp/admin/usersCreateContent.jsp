<%@ include file="../include.jsp" %>
<div class="main-content">
    <form:form method="POST" id="userForm" enctype="multipart/form-data" action="/admin/edit" class="handle-upload" modelAttribute="userForm" >

        <div class="row">
             <div class="col-sm-6">
                  <form:label path="id"><strong>Id</strong></form:label>
             </div>
             <div class="col-sm-6">
                 <form:input path="id" type="hidden" />
                 ${userForm.id}
            </div>
        </div>
        <hr/>
        <spring:bind path="username">
         <div class="row">
            <div class="col-sm-6">
                <form:label path="username"><strong>User Id</strong></form:label>
            </div>
            <div class="col-sm-6">
               <form:input path="username" /><br/>
                <form:errors path="username" class="text-danger"></form:errors>
            </div>
        </div>
        </spring:bind>
        <hr/>
        <spring:bind path="password">
        <div class="row">
            <div class="col-sm-6">
                <form:label path="password"><strong>Password</strong></form:label>
            </div>
            <div class="col-sm-6">
                <form:input type="password" path="password" /><br/>
                 <form:errors path="password" class="text-danger"></form:errors>
            </div>
        </div>
        </spring:bind>
         <spring:bind path="passwordConfirm">
            <div class="row">
                <div class="col-sm-6">
                    <form:label path="passwordConfirm"><strong>Password Confirm</strong></form:label>
                </div>
                <div class="col-sm-6">
                    <form:input type="password" path="passwordConfirm" /><br/>
                    <form:errors path="passwordConfirm" class="text-danger"></form:errors>
                </div>
            </div>
        </spring:bind>
        <c:choose>
             <c:when test="${userForm.status}">
                 <c:set var="varChecked" value="'checked'"></c:set>
             </c:when>
             <c:otherwise>
                 <c:set var="varChecked" value=""></c:set>
             </c:otherwise>
         </c:choose>
        <hr/>
        <div class="row">
             <div class="col-sm-6">
                 <form:label path="status"><strong>Active</strong></form:label>
             </div>
             <div class="col-sm-6">
                 <form:checkbox path="status" checked="${varChecked}" />
             </div>
         </div>
         <hr/>
         <div class="row">
             <div class="col-sm-12">
                 <c:forEach items="${roles}" var="role">
                     <spring:eval expression="@environment.getProperty('${role.identifier}')" var="roleName" />
                     <c:choose>
                         <c:when test="${fn:contains( userForm.roles, role ) }">
                           <span> &nbsp;&nbsp;<strong>${roleName}</strong> <input type="checkbox" id="${role.identifier}" name="roles" value="${role.id}" checked="checked">&nbsp;&nbsp;</span><br/><br/>
                         </c:when>
                         <c:otherwise>
                            <span>&nbsp;&nbsp;<strong>${roleName}</strong> <input type="checkbox" id="${role.identifier}" name="roles" value="${role.id}">&nbsp;&nbsp;</span><br/><br/>
                         </c:otherwise>
                     </c:choose>
                 </c:forEach>
              </div>
          </div>
          <hr/>
         <div class="row">
             <div class="col-sm-12">
                <input type="submit" value="Submit" onClick="ajaxformSubmit('userForm');"/>
              </div>
         </div>
       </form:form>
    </div>
  </div>
</div>