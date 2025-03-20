<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/category/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/category')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" title="Save" type="button">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-4">
                <form:input path="categoryId" id="categoryId" class="inputbox-cheil" placeholder="Category Id" />
                <form:errors path="categoryId" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                <span>Short description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                 <select name="subsidiary"  class="3col active cheil-select" id="selectpicker">
                    <option value="">Select subsidiary</option>
                    <c:forEach items="${subsidiaries}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.subsidiary, child ) }">
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
                <div class="col-sm-3">
                    <select id="parentId" name="parentId"  class="3col active cheil-select">
                        <option value="">Select super category</option>
                        <c:forEach items="${categories}" var="child">
                            <c:choose>
                                <c:when test="${editForm.parentId == child.categoryId }">
                                  <option value="${child.categoryId}" selected>${child.categoryId}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${child.categoryId}" >${child.categoryId}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-sm-3">
                    <select id="parentCategory" name="childId" class="3col active cheil-select">
                        <option value="">Select subcategory</option>
                        <c:forEach items="${categories}" var="child">
                            <c:choose>
                                <c:when test="${editForm.childId == child.categoryId }">
                                  <option value="${child.categoryId}" selected>${child.categoryId}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${child.categoryId}" >${child.categoryId}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-sm-6"></div>
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
           var multipleCancelButton2 = new Choices('#parentCategory', {
                        removeItemButton: true,
                        maxItemCount:-1,
                        searchResultLimit:20,
                        renderChoiceLimit:-1
                      });
           var multipleCancelButton3 = new Choices('#parentId', {
                       removeItemButton: true,
                       maxItemCount:-1,
                       searchResultLimit:20,
                       renderChoiceLimit:-1
                     });

   </script>

<%--script>
  $(function () {
      $('#parentCategory').change(function () {
            $('#parentId').val($(this).val());
      });

      $('#subCategory').change(function () {
            $('#categoryId').val($(this).val());
      });

      $('#parentCategory').val($('#parentId').val());
      $('#subCategory').val($('#categoryId').val());
    });
</script --%>