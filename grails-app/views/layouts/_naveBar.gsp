<div id="navbar" class="navbar navbar-default navbar-collapse h-navbar navbar-fixed-top">
    <div class="navbar-container" id="navbar-container">

        <div class="navbar-header pull-left" data-toggle="collapse" data-target=".navbar-buttons,.navbar-menu">
            <span class="navbar-brand">
                <small>
                    <g:message code="default.applicationNameWithVersion.label" args="[g.meta(name: 'info.app.version')]"
                               default="PCP Application"/>
                    <g:img dir="images" file="nesser.png" width="20" height="20"/>
                </small>
            </span>
            %{--<button class="pull-right navbar-toggle collapsed" type="button" data-toggle="collapse" data-target="#sidebar">--}%
            %{--<span class="sr-only">Toggle sidebar</span>--}%

            %{--<span class="icon-bar"></span>--}%

            %{--<span class="icon-bar"></span>--}%

            %{--<span class="icon-bar"></span>--}%
            %{--</button>--}%
        </div>

        <div class="navbar-buttons navbar-header pull-right  collapse navbar-collapse" role="navigation">
            <ul class="nav ace-nav">

                <li class="light-blue user-min">
                    <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                        <i class="icon-police"></i>
                        <g:message code="default.user.label" default="User"/>
                    </a>
                    <ul class="dropdown-menu-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                        %{--<li>--}%
                        %{--<a href="#">--}%
                        %{--<i class="ace-icon fa fa-cog"></i>--}%
                        %{--Settings--}%
                        %{--</a>--}%
                        %{--</li>--}%

                        <li>
                            <a href="${createLink(controller: 'home', action: 'profile')}">
                                <i class="ace-icon fa fa-user"></i>
                                <g:message code="default.userInfo.label" default="Info"/>
                            </a>
                        </li>

                        <li class="divider"></li>

                        <li>
                            <a href="<g:createLink controller="logout" action="index"/>">
                                <i class="ace-icon icon-power"></i>
                                <g:message code="default.button.logout.label" default="logout"/>
                            </a>
                        </li>
                    </ul>
                </li>
                <g:notifications/>
            </ul>
        </div>

    </div><!-- /.navbar-container -->
</div>
