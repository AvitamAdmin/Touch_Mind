<%@ include file="../include.jsp" %>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/category/edits" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/category')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                    </div>
                </div>
            </div>
            </br>
            </br>
            <c:forEach items="${dataToEdit}" var="childData" varStatus="loop">
            <form:input path="categoryFormList[${loop.index}].id" value="${childData.id}" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
            <div class="row">
                <div class="col-sm-2">
                    <form:input path="categoryFormList[${loop.index}].identifier" value="${childData.identifier}" class="inputbox-cheil-small" placeholder="Id/Name" required="required" />
                    <form:errors path="identifier" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-2">
                    <form:input path="categoryFormList[${loop.index}].shortDescription" value="${childData.shortDescription}" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                    <form:errors path="shortDescription" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-2">
                     <select name="categoryFormList[${loop.index}].subsidiaries"  class="3col active cheil-select" id="selectpicker2">
                        <option value="">Select Subsidiaries</option>
                        <c:forEach items="${subsidiaries}" var="child">
                            <c:choose>
                                <c:when test="${fn:contains( childData.subsidiaries, child ) }">
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
                    <select id="parentId" name="categoryFormList[${loop.index}].parentId" placeholder="Select super category"class="3col active cheil-select">
                        <option value="">Select super category</option>
                        <c:forEach items="${categories}" var="child">
                            <c:choose>
                                <c:when test="${childData.parentId == child.identifier }">
                                  <option value="${child.identifier}" selected>${child.identifier}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${child.identifier}" >${child.identifier}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-sm-3">
                    <select name="categoryFormList[${loop.index}].childId[]" class="cheil-select" placeholder="Select subcategories" multiple id="selectpicker">
                        <c:forEach items="${categories}" var="child">
                            <c:choose>
                                <c:when test="${fn:contains( childData.childId, child.identifier ) }">
                                  <option value="${child.identifier}" selected>${child.identifier}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${child.identifier}" >${child.identifier}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>
            </div>
            </c:forEach>
           </form:form>


<script type="text/javascript">
var multipleCancelButton = new Choices('#selectpicker', {
          removeItemButton: true,
          maxItemCount:-1,
          searchResultLimit:20,
          renderChoiceLimit:-1
        });
    var multipleCancelButton = new Choices('#selectpicker2', {
              removeItemButton: true,
              maxItemCount:-1,
              searchResultLimit:20,
              renderChoiceLimit:-1
            });
    var multipleCancelButton = new Choices('#parentId', {
              removeItemButton: true,
              maxItemCount:-1,
              searchResultLimit:20,
              renderChoiceLimit:-1
            });
</script>