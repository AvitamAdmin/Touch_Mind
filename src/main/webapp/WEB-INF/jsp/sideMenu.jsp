<nav class="sidebar sidebar-offcanvas" id="sidebar">
<ul class="nav flex-column flex-nowrap overflow-hidden firstmenu">
    <li class="nav-item">
        <a class="nav-link" href="#" onclick="javascript:fire_ajax_submit('/profile')">
            <i class="ti-user menu-icon"></i>
            <span class="menu-title">
                Hi
                <span> ${pageContext.request.userPrincipal.name} </span>
            </span>
        </a>
    </li>
    <a style="text=align:center;" class="nav-link" href="/logout">
        <span style="font-weight:bold;color:white;" class="menu-title">
            Logout
        </span>
    </a>
    <c:forEach var="node" items="${nodes}" varStatus="loop">
        <c:if test="${empty node.parentNode}">
            <li class="nav-item">
                <a class="nav-link collapsed text-truncate" id="firstmenu${loop.index}" href="#" data-toggle="collapse" data-target="#submenu1${loop.index}"> <i class="ti-menu menu-icon"></i> <span class="d-sm-inline">${node.identifier}</span></a>

                <c:if test="${not empty node.childNodes}">
                <div class="collapse" id="submenu1${loop.index}" aria-expanded="false">
                    <ul class="flex-column pl-2 nav secondmenu">
                        <c:forEach var="childNode" items="${node.childNodes}" varStatus="loop2">
                            <li class="nav-item">
                                <a class="nav-link text-truncate collapsed py-1" id="secondmenu${loop2.index}" href="#" onclick="javascript:fire_ajax_submit('${childNode.path}')"><i class="ti-menu-alt menu-icon"></i><span>${childNode.identifier}</span></a>
                            </li>
                        </c:forEach>
                        </ul>
                    </div>
                </c:if>
            </li>
        </c:if>
    </c:forEach>
</ul>

</nav>

<script>
$(".secondmenu li a").click(function() {
        $('#sidebar').removeClass('active');
        $('.secondmenu li').find('a').each(function() {
            $(this).removeAttr('aria-expanded');
        });
        $(this).attr('aria-expanded', true);
    });
    $('[id*="firstmenu"]').click(function() {
        var flag = 'true';
        if($(this).attr('aria-expanded')=='true'){
          flag = 'false';
        }
        $('.firstmenu li').find('a').each(function() {
            $(this).attr('aria-expanded', false);
            $(this).addClass('collapsed');
            $(this).closest("li").find('div').removeClass('show');
        });
        if(flag=='false'){
            $(this).removeClass('collapsed');
            $(this).closest("li").find('div').addClass('show');
        }
          $(this).attr('aria-expanded', flag);
        });
</script>