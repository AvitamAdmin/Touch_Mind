<%@ include file="../include.jsp" %>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/model/edits" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/model')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                    </div>
                </div>
            </div>
            </br>
            </br>
            <c:forEach items="${dataToEdit}" var="childData" varStatus="loop">
            <form:input path="modelFormList[${loop.index}].id" value="${childData.id}" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
            <div class="row">
                <div class="col-sm-3">
                    <form:input path="modelFormList[${loop.index}].identifier" value="${childData.identifier}" class="inputbox-cheil-small" placeholder="Id/Name" required="required" />
                    <form:errors path="identifier" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3">
                    <form:input path="modelFormList[${loop.index}].shortDescription" value="${childData.shortDescription}" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                    <form:errors path="shortDescription" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3">
                     <select class="cheil-select" name="subsidiaries[]" placeholder="Select subsidiaries"  multiple id="selectpicker2">
                      <span>Select subsidiaries</span>
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
                <div class="col-sm-3">
                    <select name="modelFormList[${loop.index}].categories[]" placeholder="Select category"class="3col active cheil-select">
                        <option value="">Select category</option>
                        <c:forEach items="${categories}" var="child">
                            <c:set var="selectedPerm"></c:set>
                               <c:forEach items="${editForm.categories}" var="childSub">
                                   <c:if test="${childSub.recordId == child.recordId}">
                                       <c:set var="selectedPerm">selected</c:set>
                                   </c:if>
                               </c:forEach>
                             <option value="${child.id}" ${selectedPerm}>${child.identifier}</option>
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