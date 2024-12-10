<%@ include file="../include.jsp" %>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/role/edits" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/role')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                    </div>
                </div>
            </div>
            </br>
            </br>
            <c:forEach items="${dataToEdit}" var="childData" varStatus="loop">
            <form:input path="roleFormList[${loop.index}].id" value="${childData.id}" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
            <div class="row">
                <div class="col-sm-3">
                    <form:input path="roleFormList[${loop.index}].name" value="${childData.name}" class="inputbox-cheil-small" placeholder="Id/Name" required="required" />
                    <form:errors path="name" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3">
                    <form:input path="roleFormList[${loop.index}].quota" value="${childData.quota}" class="inputbox-cheil-long" placeholder="Quota" />
                    <form:errors path="quota" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3">
                    <form:input path="roleFormList[${loop.index}].quotaUsed" value="${childData.quotaUsed}" class="inputbox-cheil-long" placeholder="Quota Used " />
                    <form:errors path="quotaUsed" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3" style="margin-top:18px;">
                    <c:choose>
                        <c:when test="${childData.published}">
                             <c:set var="published" value="checked"></c:set>
                         </c:when>
                         <c:otherwise>
                             <c:set var="unpublished" value="checked"></c:set>
                         </c:otherwise>
                    </c:choose>
                    <input type="radio" name="roleFormList[${loop.index}].published"  value="true" ${published}> Published
                    <input type="radio" name="roleFormList[${loop.index}].published" value="false" ${unpublished}> Unpublished
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