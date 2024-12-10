<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/user/edit" class="handle-upload" modelAttribute="editForm" >
        <form:input path="id" type="hidden" />
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" title="Save" type="button">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
            <form:input path="username" class="inputbox-cheil-small" placeholder="Email" readonly="true"></form:input>
            <label for="username" class="col-form-label" style="position: absolute;margin-left: -30px;"></label>
            <span>Email</span>
            <form:errors path="username" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="email" class="inputbox-cheil" placeholder="User Name" readonly="true"></form:input>
                <label for="email" class="col-form-label" style="position: absolute;margin-left: -30px;"></label>
                <span>User Name</span>
                <form:errors path="email" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                 <select name="locale"  class="3col active cheil-select" id="selectpicker3" disabled>
                    <option value="">Select locale</option>
                    <c:forEach items="${countries}" var="child">
                        <c:choose>
                            <c:when test="${editForm.locale eq child.locale }">
                              <option value="${child.locale}" selected>${child.locale}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.locale}" >${child.locale}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-3">
             <select class="cheil-select" name="node" id="selectpicker" disabled>
                <option value="">Select Node</option>
                <c:forEach items="${nodes}" var="child">
                    <c:choose>
                        <c:when test="${editForm.node eq child.id}">
                          <option value="${child.id}" selected>${child.identifier}</option>
                        </c:when>
                        <c:otherwise>
                           <option value="${child.id}" >${child.identifier}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </div>
        </div>
        <br/>
        <br/>

        <div class="row">
        <c:if test="${isAdmin}">
            <div class="col-sm-3">
                 <select class="cheil-select" name="subsidiaries[]" placeholder="Select subsidiaries"  multiple id="selectpicker2" disabled>
                 <span>Select subsidiaries</span>
                    <c:forEach items="${subsidiaries}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.subsidiaries, child ) }">
                              <option value="${child.id}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.id}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-3 d-flex justify-content-center mt-100">
                <select class="cheil-select" name="roles[]" placeholder="Select Roles" multiple id="selectpicker" autocomplete="false" disabled>
                <span>Select Roles</span>
                    <c:forEach items="${roles}" var="role">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.roles, role ) }">
                              <option value="${role.id}" selected>${role.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${role.id}" >${role.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            </c:if>
            <div class="col-sm-3">
                <form:input type="password" path="password" class="inputbox-cheil-small" placeholder="Enter Password" autocomplete="false" readonly="true"/>
                <span>Password</span>

                </div>
            <div class="col-sm-3">
                <form:input type="password" path="passwordConfirm" class="inputbox-cheil" placeholder="Confirm Password" autocomplete="false" readonly="true"/>
             <span>Confirm Password</span>
            </div>
        </div>

        <script type="text/javascript">
            $(document).ready(function() {

                var multipleCancelButton = new Choices('#selectpicker', {
                        removeItemButton: true,
                        maxItemCount:-1,
                        searchResultLimit:20,
                        renderChoiceLimit:-1
                      });
                var multipleCancelButton2 = new Choices('#selectpicker2', {
                        removeItemButton: true,
                        maxItemCount:-1,
                        searchResultLimit:20,
                        renderChoiceLimit:-1
                      });
                var multipleCancelButton3 = new Choices('#selectpicker3', {
                                        removeItemButton: true,
                                        maxItemCount:-1,
                                        searchResultLimit:20,
                                        renderChoiceLimit:-1
                                      });
            });
        </script>

       </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
    </div>
  </div>
</div>