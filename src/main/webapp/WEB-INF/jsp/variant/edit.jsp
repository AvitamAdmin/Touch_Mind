<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/variant/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/variant')" aria-controls="tableData" type="button" title="Cancel">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" type="button" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-4">
            <form:input path="identifier" class="inputbox-cheil-small" placeholder="Id/Name" />
            <span>Id/Name</span>
            <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                <span>Short description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
             <div class="col-sm-3">
                 <select name="subsidiaries[]"  id="selectpicker2" multiple class="3col active cheil-select">
                    <option value="">Select subsidiaries</option>
                   <c:forEach items="${subsidiaries}" var="child">
                     <c:set var="selectedPerm"></c:set>
                     <c:forEach items="${editForm.subsidiaries}" var="childSub">
                         <c:if test="${childSub.recordId == child.recordId}">
                             <c:set var="selectedPerm">selected</c:set>
                         </c:if>
                     </c:forEach>
                   <option value="${child.id}" ${selectedPerm}>${child.identifier}</option>
                 </c:forEach>
                </select>
            </div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-4">
                <form:input path="externalProductUrl" class="inputbox-cheil-long" placeholder="External Product Url" />
                <span>Product Url</span>
                <form:errors path="externalProductUrl" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <select name="category"  class="3col active cheil-select" id="selectpicker2">
                <option value="">Select category</option>
                    <c:forEach items="${categories}" var="child">
                        <c:choose>
                            <c:when test="${editForm.category.recordId == child.recordId}">
                              <option value="${child.id}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.id}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
             <div class="col-sm-4">
                 <select name="model"  class="3col active cheil-select" id="selectpicker3">
                     <option value="">Select model</option>
                     <c:forEach items="${models}" var="child">
                         <c:choose>
                             <c:when test="${editForm.model.recordId == child.recordId}">
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

        <br/><br/>
        <div class="row">
                <div class="col-sm-4">
                 <select name="pageType"  class="3col active cheil-select" placeholder="Select Page type" id="selectpicker4">
                     <option value="" selected>Select Page type</option>
                     <option value="BC" <c:if test="${editForm.pageType=='BC'}">selected</c:if>>BC</option>
                     <option value="PD" <c:if test="${editForm.pageType=='PD'}">selected</c:if>>PD</option>
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
   </script>