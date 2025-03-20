<%@ include file="../include.jsp" %>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/module/edits" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/module')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                    </div>
                </div>
            </div>
            </br>
            </br>
           <c:forEach items="${dataToEdit}" var="childData" varStatus="loop">
                       <form:input path="libraryFormList[${loop.index}].id" value="${childData.id}" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
                       <div class="row">
                           <div class="col-sm-2">
                               <form:input path="libraryFormList[${loop.index}].id" value="${childData.id}" class="inputbox-cheil-small" placeholder="Enter ID without space" required="required" />
                               <form:errors path="id" class="text-danger"></form:errors>
                           </div>
                           <div class="col-sm-3">
                               <form:input path="libraryFormList[${loop.index}].shortDescription" value="${childData.shortDescription}" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                               <form:errors path="shortDescription" class="text-danger"></form:errors>
                           </div>
                           <div class="col-sm-3">
                                <select name="libraryFormList[${loop.index}].systemId"  class="3col active cheil-select" id="selectpicker">
                                    <c:forEach items="${systems}" var="child">
                                         <c:choose>
                                          <c:when test="${editForm.systemId eq child.id }">
                                           <option value="${child.id}" selected>${child.shortDescription}</option>
                                          </c:when>
                                          <c:otherwise>
                                            <option value="${child.id}" >${child.shortDescription}</option>
                                          </c:otherwise>
                                         </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                           <div class="col-sm-2">
                               <form:input path="libraryFormList[${loop.index}].systemPath" class="inputbox-cheil-small" placeholder="Enter the system path" />
                               <form:errors path="systemPath" class="text-danger"></form:errors>
                           </div>
                           <div class="col-sm-2">
                              <form:input path="libraryFormList[${loop.index}].systemLink" class="inputbox-cheil-small" placeholder="Enter the system link" />
                              <form:errors path="systemLink" class="text-danger"></form:errors>
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