<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/module/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/module')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
       <%@ include file="../commonFields.jsp" %>
               <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>

        <div class="row">
            <div class="col-sm-3">
                <form:input path="id" class="inputbox-cheil-small" placeholder="Enter ID without space" required="required"/>
                <span>Mod ID</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Identifier" required="required"/>
                <span>identifier</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Input a short description" required="required" />
                <span>Short description</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
             <div class="col-sm-3">
                 <select name="systemId"  id="selectpicker" class="3col active cheil-select" required="required" placeholder="Select system">
                     <c:forEach items="${systems}" var="child">
                         <c:choose>
                             <c:when test="${editForm.systemId eq child.recordId }">
                               <option value="${child.recordId}" selected>${child.identifier}</option>
                             </c:when>
                             <c:otherwise>
                                <option value="${child.recordId}" >${child.identifier}</option>
                             </c:otherwise>
                         </c:choose>
                     </c:forEach>
                 </select>
             </div>

        </div>
        <br/>
        <br/>
        <div class="row">
            <div class="col-sm-9">
                <form:input path="systemLink" class="inputbox-cheil" placeholder="Enter the system link" />
                <span>System link</span>
                <form:errors path="systemLink" class="text-danger"></form:errors>
            </div>
        </div>
        <br/>
        <br/>
        <div class="row">
            <div class="col-sm-12">
                <form:textarea id="summernote" rows="5" path="systemPath" class="inputbox-cheil-textarea" placeholder="Enter the system path" />
                <form:errors path="errorType" class="text-danger"></form:errors>
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
var multipleCancelButton2 = new Choices('#selectpicker', {
                                        removeItemButton: true,
                                        maxItemCount:-1,
                                        searchResultLimit:20,
                                        renderChoiceLimit:-1
                                      });
   $(document).ready(function() {
      $('#summernote').summernote({
           placeholder: 'Enter the system path',
           tabsize: 2,
           toolbar: [
                       [ 'style', [ 'style' ] ],
                       [ 'font', [ 'bold', 'italic', 'underline', 'clear'] ],
                       [ 'fontname', [ 'fontname' ] ],
                       [ 'fontsize', [ 'fontsize' ] ],
                       [ 'color', [ 'color' ] ],
                       [ 'para', [ 'ol', 'ul', 'paragraph', 'height' ] ],
                       [ 'table', [ 'table' ] ],
                   ]
         });
   });
   </script>