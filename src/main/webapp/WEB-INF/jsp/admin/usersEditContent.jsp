<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/user/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/user')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" title="Save" type="button">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3" id="usernameInputDiv">
            <form:input path="username" class="inputbox-cheil-small" placeholder="Email" id="usernameInput"></form:input>
            <label for="username" class="col-form-label" style="position: absolute;margin-left: -30px;"><i class="far fa-eye-slash" id="usernameInputBtn" style="display:none"></i></label>
            <span>Email</span>
            <form:errors path="username" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3" style="display:none" id="usernameMaskedInputDiv">
            <form:input path="usernameMasked" class="inputbox-cheil-small" placeholder="Email" id="usernameMaskedInput" readonly="true"></form:input>
            <label for="usernameMasked" class="col-form-label" style="position: absolute;margin-left: -30px;"><i class="far fa-eye" id="usernameMaskedInputBtn" style="display:none"></i></label>
            <span>Email</span>
            <form:errors path="usernameMasked" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3" style="display:none" id="emailMaskedInputDiv">
                <form:input path="emailMasked" class="inputbox-cheil" placeholder="User Name" id="emailMaskedInput" readonly="true"></form:input>
                <label for="emailMasked" class="col-form-label" style="position: absolute;margin-left: -30px;"><i class="far fa-eye" id="emailMaskedInputBtn" style="display:none"></i></label>
                <span>User Name</span>
                <form:errors path="emailMasked" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3" id="emailInputDiv">
                <form:input path="email" class="inputbox-cheil" placeholder="User Name" id="emailInput"></form:input>
                <label for="email" class="col-form-label" style="position: absolute;margin-left: -30px;"><i class="far fa-eye-slash" id="emailInputBtn" style="display:none"></i></label>
                <span>User Name</span>
                <form:errors path="email" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                 <select name="locale"  class="3col active cheil-select" id="selectpicker3">
                    <option value="">Select locale</option>
                    <c:forEach items="${countries}" var="child">
                        <c:choose>
                            <c:when test="${editForm.locale eq child.locale }">
                              <option value="${child.locale}" selected>${child.locale}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.locale}" >${child.locale}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
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
        </div>
        <br/>
        <br/>

        <div class="row">
        <c:if test="${isAdmin}">
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
            <div class="col-sm-3 d-flex justify-content-center mt-100">
                <select class="cheil-select" name="roles[]" placeholder="Select Roles" multiple id="selectpicker" autocomplete="false">
                <span>Select Roles</span>
                    <c:forEach items="${roles}" var="role">
                    <c:set var="selectedPerm"></c:set>
                    <c:forEach items="${editForm.roles}" var="userRole">
                    <c:if test="${userRole.recordId == role.recordId}">
                        <c:set var="selectedPerm">selected</c:set>
                    </c:if>

                    </c:forEach>
                    <option value="${role.id}" ${selectedPerm}>${role.identifier}</option>
                    </c:forEach>
                </select>
            </div>
            </c:if>
            <div class="col-sm-3">
                <form:input type="text" path="password" class="inputbox-cheil-small" placeholder="Enter Password" autocomplete="false" onfocus="this.setAttribute('type', 'password')"/>
                <span>Password</span>

                </div>
            <div class="col-sm-3">
                <form:input type="password" path="passwordConfirm" class="inputbox-cheil" placeholder="Confirm Password" autocomplete="false"/>
             <span>Confirm Password</span>
            </div>
        </div>

        <script type="text/javascript">
            $(document).ready(function() {
                        var emailVal = $('#emailInput').val();
                        var usernameInput = $('#usernameInput').val();
                        if(emailVal.indexOf('@') > -1){
                           var val = "******";
                           var val2 = emailVal.split('@')[1];
                           $('#emailMaskedInput').val(val + '@' + val2);
                           $('#emailMaskedInputDiv').show();
                           $('#emailMaskedInputBtn').show();
                           $('#emailInputDiv').hide();
                           $('#emailInputBtn').hide();
                       }

                       if(usernameInput.indexOf('@') > -1){
                            var val = "******";
                            var val2 = usernameInput.split('@')[1];
                            $('#usernameMaskedInput').val(val + '@' + val2);
                            $('#usernameMaskedInputDiv').show();
                            $('#usernameMaskedInputBtn').show();
                            $('#usernameInputDiv').hide();
                            $('#usernameInputBtn').hide();
                        }
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
            });


             $('#emailInput').on("change paste", function() {
                   var value = $(this).val();
                   $('#emailMaskedInput').val(value);
                   if(value.indexOf('@') > -1){
                       var val = "******";
                       var val2 = value.split('@')[1];
                       $('#emailMaskedInput').val(val + '@' + val2);
                       $('#emailMaskedInputDiv').show();
                       $('#emailMaskedInputBtn').show();
                       $('#emailInputDiv').hide();
                       $('#emailInputBtn').hide();
                   }
              });
              $('#usernameInput').on("change paste", function() {
                 var value = $(this).val();
                 $('#usernameMaskedInput').val(value);
                 if(value.indexOf('@') > -1){
                     var val = "******";
                     var val2 = value.split('@')[1];
                     $('#usernameMaskedInput').val(val + '@' + val2);
                     $('#usernameMaskedInputDiv').show();
                     $('#usernameMaskedInputBtn').show();
                     $('#usernameInputDiv').hide();
                     $('#usernameInputBtn').hide();
                 }
            });

            $('#usernameInputBtn').click(function () {
                                 $('#usernameMaskedInputDiv').show();
                                 $('#usernameMaskedInputBtn').show();
                                 $('#usernameInputDiv').hide();
                        });
            $('#usernameMaskedInputBtn').click(function () {
                     $('#usernameMaskedInputDiv').hide();
                     $('#usernameInputDiv').show();
                     $('#usernameInputBtn').show();
            });
            $('#emailInputBtn').click(function () {
                     $('#emailMaskedInputDiv').show();
                     $('#emailMaskedInputBtn').show();
                     $('#emailInputDiv').hide();
            });
            $('#emailMaskedInputBtn').click(function () {
                     $('#emailMaskedInputDiv').hide();
                     $('#emailInputDiv').show();
                     $('#emailInputBtn').show();
            });
        </script>

       </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
    </div>
  </div>
</div>