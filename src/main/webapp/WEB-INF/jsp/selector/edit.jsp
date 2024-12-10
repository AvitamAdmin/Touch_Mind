<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/selector/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/selector')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Id" required="required" />
                <span>Enter Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Enter Description" />
                <span>Enter Description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <select name="testPlan" class="3col active cheil-select" id="selectpicker">
                    <option value="">Select a test plan</option>
                    <c:forEach items="${testPlans}" var="child">
                        <c:choose>
                            <c:when test="${editForm.testPlan == child.id}">
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
        <c:forEach items="${editForm.selectorConfigs}" var="child" varStatus="loop">
            <c:if test="${child!=''}">
                <div class="row"  id="inputParamRow${loop.index}">
                    <div class="col-sm-2 control-group input-group">
                        <input name="selectorConfigs[${loop.index}].identifier" value="${editForm.selectorConfigs[loop.index].identifier}" class="inputbox-cheil-small" placeholder="identifier" />
                    </div>
                    <div class="col-sm-2 cheil-select" id="pageInputDiv">
                        <select name="selectorConfigs[${loop.index}].className" data-method-dropdown="#actionInput${loop.index}" onchange="onClassSelected(this.value, ${loop.index})" class="3col active">
                            <option value="" selected>Select class</option>
                            <c:forEach items="${dataSet}" var="dataItem"> <!-- list of classes is sent from the controller -->
                                <option value="${dataItem}" <c:if test="${dataItem == editForm.selectorConfigs[loop.index].className}">selected</c:if>>${dataItem}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-sm-2 cheil-select" id="actionInputDiv${loop.index}">
                        <select name="selectorConfigs[${loop.index}].methodName" class="3col active" id="actionInput${loop.index}"></select>
                    </div>
                    <div class="col-sm-1 cheil-select" id="strategyInputDiv">
                        <select name="selectorConfigs[${loop.index}].strategy" data-method-dropdown="#actionInput${loop.index}" class="3col active">
                            <!--<option value="" selected>Select strategy</option>-->
                            <c:forEach items="${selectorStrategies}" var="strategyItem"> <!-- list of strategies is sent from the controller -->
                                <option value="${strategyItem}" <c:if test="${strategyItem == editForm.selectorConfigs[loop.index].strategy}">selected</c:if>>${strategyItem}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-sm-3 control-group input-group">
                        <input name="selectorConfigs[${loop.index}].uiSelector" value="${editForm.selectorConfigs[loop.index].uiSelector}" class="inputbox-cheil-small" placeholder="Value" />
                    </div>
                    <div class="col-sm-1"> <img style="width:32px;height:32px;" onclick="javascript:removeRow(${loop.index})" src="${contextPath}/images/remove.png" /></div>
                    <script type="text/javascript">
                        $(document).ready(function() {
                            // Trigger onClassSelected function for the current row to populate dropdowns with values
                            onClassSelected('${editForm.selectorConfigs[loop.index].className}', ${loop.index}, '${editForm.selectorConfigs[loop.index].methodName}');
                        });
                    </script>
                </div>
                </br>
            </c:if>
        </c:forEach>
        <div class="input-group control-group after-add-input-more">
            <button class="btn btn-primary add-input-more" type="button"><i class="glyphicon glyphicon-add"></i>Add an input field</button>
        </div>
        <input id="existingParamsCount" name="existingParamsCount" value="${existingParamsCount}" style="display:none;">

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

    var multipleCancelButton3 = new Choices('#selectpicker', {
        removeItemButton: true,
        maxItemCount:-1,
        searchResultLimit:20,
        renderChoiceLimit:-1
    });

    $(document).ready(function() {
        var count = $('#existingParamsCount').val();
        $(".add-more").click(function(){
            var html = $(".copy").html();
            $(".after-add-more").before(html);
        });

        $(".add-input-more").click(function(){
            count++;
            var html = '<div class="row" id="inputParamRow' + count + '">'
                +'<div class="col-sm-2 control-group input-group">'
                +'<input name="selectorConfigs[' + count + '].identifier" class="inputbox-cheil-small" placeholder="identifier"/></div>'
                + '<div class="col-sm-2 cheil-select" id="pageInputDiv">'
                + '<select name="selectorConfigs[' + count + '].className" onchange="onClassSelected(this.value, ' + count + ')" class="3col active">'
                + '<option value="" selected>Select page</option>'
                + '<c:forEach items="${dataSet}" var="dataItem">'
                + '<option value="${dataItem}">${dataItem}</option>'
                + '</c:forEach>'
                + '</select>'
                + '</div>'
                + '<div class="col-sm-2 cheil-select" id="actionInputDiv' + count + '">'
                + '<select name="selectorConfigs[' + count + '].methodName" id="actionInput' + count + '" class="3col active"></select>'
                + '</div>'
                + '<div class="col-sm-1 cheil-select" id="strategyInputDiv">'
                + '<select name="selectorConfigs[' + count + '].strategy" class="3col active">'
               // + '<option value="" selected>Select strategy</option>'
                + '<c:forEach items="${selectorStrategies}" var="strategyItem">'
                + '<option value="${strategyItem}">${strategyItem}</option>'
                + '</c:forEach>'
                + '</select>'
                + '</div>'
                + '<div class="col-sm-3">    <input type="text" name="selectorConfigs['+count+'].uiSelector" class="inputbox-cheil" placeholder="Enter the value">    <span>Enter the value</span></div>'
                + '<div class="col-sm-1">'
                + '<img style="width: 32px; height: 32px;" onclick="removeRow(' + count + ')" src="${contextPath}/images/remove.png" />'
                + '</div>'
                + '</div>'
                + '<br />';
            $(".after-add-input-more").before(html);
        });

        $("body").on("click",".remove",function(){
            $(this).parents(".control-group").remove();
        });

        $(document).on("click","#inputParamRow",function(){
            $(this).remove();
        });

    });

    function onClassSelected(value, countVal, presetMethodValue) {
        if(value!=''){
            // Store the selected page value in a data attribute
            $('#inputParamRow' + countVal).data('selectedPage', value);

            $.ajax({
                type: 'GET',
                url: "/admin/testplanbuilder/getMethodName/" + value,
                datatype: "json",
                success: function(data){
                    var toAppend='<option value="">Select action</option>';
                    for(var i=0; i<data.length; i++){
                        toAppend+='<option value="'+data[i]+'">'+ data[i] +'</option>'
                    }
                    $('#actionInput'+countVal).empty().append(toAppend); //populating the dropdown with data

                    // Check if a presetMethodValue is provided and set it as the selected value
                    if (presetMethodValue !== undefined) {
                        $('#actionInput' + countVal).val(presetMethodValue);
                    }
                },
                error:function(e){
                    console.log(e.statusText);
                }
            });
        }
    }

    function removeRow(countVal) {
        $('#inputParamRow'+countVal).remove();
    }

</script>