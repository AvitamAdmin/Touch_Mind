<%@ include file="../include.jsp" %>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/interface/edits" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/interface')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                    </div>
                </div>
            </div>
            </br>
            </br>
            <c:forEach items="${dataToEdit}" var="childData" varStatus="loop">
            <form:input path="interfaceFormList[${loop.index}].id" value="${childData.id}" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
            <div class="row">
                <div class="col-sm-2">
                    <form:input path="interfaceFormList[${loop.index}].name" value="${childData.name}" class="inputbox-cheil-small" placeholder="Id/Name" required="required" />
                    <form:errors path="name" class="text-danger"></form:errors>
                </div>
                <form:input path="interfaceFormList[${loop.index}].recordId" type="hidden" value="${childData.recordId}"/>
                <div class="col-sm-3">
                    <form:input path="interfaceFormList[${loop.index}].path" value="${childData.path}" class="inputbox-cheil-long" placeholder="path" />
                    <form:errors path="path" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3">
                                <select class="cheil-select" name="interfaceFormList[${loop.index}].parentNode.recordId" id="selectpicker">
                                   <option value="">Select node</option>
                                   <c:forEach items="${nodes}" var="child">
                                       <c:choose>
                                           <c:when test="${childData.parentNode.recordId eq child.recordId}">
                                             <option value="${child.recordId}" selected>${child.identifier}</option>
                                           </c:when>
                                           <c:otherwise>
                                              <option value="${child.recordId}" >${child.identifier}</option>
                                           </c:otherwise>
                                       </c:choose>
                                   </c:forEach>
                               </select>
                           </div>
                           <div class="col-sm-3">
                           <form:input path="interfaceFormList[${loop.index}].id" value="${childData.displayPriority}" class="inputbox-cheil-small" placeholder="Display order"/>
                               <form:errors path="displayPriority" class="text-danger"></form:errors>
                           </div>
            </div>
            </c:forEach>
           </form:form>


<script type="text/javascript">
         var multipleCancelButton = new Choices('#selectpicker', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1,
             placeholder: true,
             placeholderValue: "Select Value"
           });
</script>