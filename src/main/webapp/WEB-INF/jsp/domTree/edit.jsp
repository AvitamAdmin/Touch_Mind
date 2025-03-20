<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/domTree/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/domTree')" aria-controls="tableData" type="button" title="Cancel">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" type="button" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
            <input name="subsidiary" class="inputbox-cheil" placeholder="Subsidiary" readonly="true" value="${editForm.subsidiary}"></input>
            <span>Subsidiary</span>
            </div>
            <div class="col-sm-3">
            <input name="site" class="inputbox-cheil" placeholder="Site" readonly="true" value="${editForm.site}"></input>
            <span>Site</span>
            </div>
            <div class="col-sm-3">
            <input name="variant" class="inputbox-cheil" placeholder="Variant" readonly="true" value="${editForm.variant}"></input>
            <span>Variant</span>
            </div>
            <div class="col-sm-3">
            <input name="node" class="inputbox-cheil" placeholder="Node" readonly="true" value="${editForm.node}"></input>
            <span>Node</span>
            </div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-3">
                <input name="shopCampaign" class="inputbox-cheil" placeholder="Campaign" readonly="true" value="${editForm.shopCampaign}"></input>
            </div>
            <div class="col-sm-3"></div>
        </div>
        <br/><br/>
        <table border="1">
            <thead>
                <tr>
                    <th>Component</th>
                    <th>Details</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${editForm.navigationTree}" var="entry">
                    <tr>
                        <td>${entry.key}</td>
                        <td>
                            <table border="1">
                                <thead>
                                    <tr>
                                        <th>Element type</th>
                                        <th>link</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${entry.value}" var="val">
                                        <tr>
                                            <td>${val.elementType}</td>
                                            <td><span <c:if test="${val.used == true}">class="th-sm"</c:if>>Selector: ${val.selector}</span></td>
                                            <td>${val.used}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
       </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
</div>

