<%@ include file="../include.jsp" %>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/campaigns/edits" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/campaigns')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                    </div>
                </div>
            </div>
            </br>
            </br>
            <c:forEach items="${dataToEdit}" var="childData" varStatus="loop">
            <form:input path="variantFormList[${loop.index}].id" value="${childData.id}" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
            <div class="row">
                <div class="col-sm-2">
                    <form:input path="variantFormList[${loop.index}].variantId" value="${childData.variantId}" class="inputbox-cheil-small" placeholder="Id/Name" required="required" />
                    <form:errors path="variantId" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-2">
                    <form:input path="variantFormList[${loop.index}].shortDescription" value="${childData.shortDescription}" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                    <form:errors path="shortDescription" class="text-danger"></form:errors>
                </div>

                <div class="col-sm-2">
                    <form:input path="variantFormList[${loop.index}].externalProductUrl" class="inputbox-cheil-long" placeholder="External Product Url" />
                    <span>Product Url</span>
                    <form:errors path="externalProductUrl" class="text-danger"></form:errors>
                </div>

                 <div class="col-sm-3">
                      <select name="variantFormList[${loop.index}].category"  class="3col active cheil-select" id="selectpicker">
                      <option value="">Select category</option>
                                          <c:forEach items="${categories}" var="child">
                                              <c:choose>
                                                  <c:when test="${fn:contains( childData.category, child ) }">
                                                    <option value="${child.id}" selected>${child.categoryId}</option>
                                                  </c:when>
                                                  <c:otherwise>
                                                     <option value="${child.id}" >${child.categoryId}</option>
                                                  </c:otherwise>
                                              </c:choose>
                                          </c:forEach>
                         </select>
                 </div>

                 <div class="col-sm-3">
                      <select name="variantFormList[${loop.index}].model"  class="3col active cheil-select" id="selectpicker">
                       <option value="">Select model</option>
                           <c:forEach items="${models}" var="child">
                               <c:choose>
                                   <c:when test="${fn:contains( childData.model, child ) }">
                                     <option value="${child.id}" selected>${child.modelId}</option>
                                   </c:when>
                                   <c:otherwise>
                                      <option value="${child.id}" >${child.modelId}</option>
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
             renderChoiceLimit:-1,
             placeholder: true,
             placeholderValue: "Select Value"
           });
</script>