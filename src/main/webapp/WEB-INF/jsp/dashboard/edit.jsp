<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/dashboard/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/dashboard')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Identifier" required="required"/>
                <span>Identifier</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                 <select class="cheil-select" name="node" id="selectpicker">
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
            <div class="col-sm-3">
                <form:input type="color" path="themeColor" class="inputbox-cheil" placeholder="Enter theme" />
                <span>Theme</span>
                <form:errors path="themeColor" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="template" class="inputbox-cheil" placeholder="Enter template" />
                <span>Template</span>
                <form:errors path="template" class="text-danger"></form:errors>
            </div>

        </div>
        <br/><br/>

        <div class="row">
                <div class="col-sm-6">
                     <select class="cheil-select" name="dashboardProfile" id="selectpicker">
                        <option value="">Select profile</option>
                        <c:forEach items="${profiles}" var="profile">
                            <c:choose>
                                <c:when test="${editForm.dashboardProfile eq profile.recordId}">
                                  <option value="${profile.recordId}" selected>${profile.identifier}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${profile.recordId}" >${profile.identifier}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-sm-6">
                         <select name="subsidiary" id="subsidiary" class="3col active cheil-select">
                            <option value="">Select subsidiary</option>
                            <c:forEach items="${subsidiaries}" var="child">
                                <c:choose>
                                    <c:when test="${editForm.subsidiary == child.id}">
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


        </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
    </div>
  </div>
</div>

 <script type="text/javascript">
       var multipleCancelButton = new Choices('#selectpicker', {
           removeItemButton: true,
           maxItemCount:-1,
           searchResultLimit:20,
           renderChoiceLimit:-1
         });
         var subsidiaryDataDynamic = new Choices('#subsidiary', {
                removeItemButton: true,
                maxItemCount:-1,
                searchResultLimit:20,
                renderChoiceLimit:-1
           });
 </script>