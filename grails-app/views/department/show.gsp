<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'department.entity', default: 'Department List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Department List')}" />
    <title>${title}</title>
</head>

<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'department', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:simpleTab>
    <lay:simpleTabElement id="departmentTab" entityName="department" entityNameAction="show" title="${message(code: 'department.label')}" icon="icon-commerical-building" isActive="true" >
        <g:render template="/department/show" model="[department: department]"/>
    </lay:simpleTabElement>
    <lay:simpleTabElement id="departmentOperationalTaskTab"
                          entityName="joinedDepartmentOperationalTasks" entityNameAction="list"
                          title="${message(code: 'operationalTask.entities', default: 'Department OperationalTask')}"
                          icon="icon-tasks">
    </lay:simpleTabElement>

    <lay:simpleTabElement id="departmentPhoneTab"
                          entityName="departmentContactInfo" entityNameAction="list"
                          title="${message(code: 'phone.entities', default: 'Department Phone')}"
                          icon="icon-phone">
    </lay:simpleTabElement>

</lay:simpleTab>


<script type="text/javascript">
    var entityName = "";
    var tabName = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "departmentTab";
        var divId = $(this).attr("href");
        tabName = divId.trim().replace("#","");

        $(".tab-content").find("div.tab-pane").each( function(){
            var id = $(this).attr('id');
            if(tabName != id && id != excludeTab){
                $(this).html('');
            }
        });

        entityName = $(this).attr("entityName");
        if(tabName != excludeTab) {
            $.post("${createLink(controller: 'tabs',action: 'loadTab')}", {
                tabName: tabName,
                holderEntityName: 'department',
                holderEntityId: "${department?.id}",
                tabEntityName: entityName
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
            });
        }else{
            $.ajax({
                url: '${createLink(controller: 'tabs',action: 'showInLine')}',
                type: 'POST',
                data: {
                    id: "${department?.id}",
                    withRemotingValues:"true",
                    tabEntityName: "department"
                },
                dataType: 'html',
                beforeSend: function(jqXHR,settings) {
                    $('.alert.page').html('');
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(data) {
                    guiLoading.hide();
                    $(divId).html(data);

                }
            });
        }
    });
    function renderInLineContactInfoCreate() {
        $.ajax({
            url: '${createLink(controller: 'department',action: 'createContactInfo')}',
            type: 'POST',
            data: {
                'department.id': "${department.id}",
                'ContactMethodEnum': $("#ContactMethodEnum").val(),
                'ownerdepartment.id': "${department.id}",
                'relateddepartment.id': "${department.id}",
                tabEntityName: "department",
                isdepartmentDisabled: true,
                isRelatedObjectTypeDisabled: true,
                isDocumentOwnerDisabled: true,
                relatedObjectType: '${ps.police.pcore.enums.v1.ContactInfoClassification.ORGANIZATION}',
                documentOwner: '${ps.police.pcore.enums.v1.RelatedParty.ORGANIZATION}'

            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (data) {
                guiLoading.hide();
                $('#' + entityName + "Div").html(data);

            }
        });
    }



    function renderInLineShow(id) {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'showInLine')}',
            type: 'POST',
            data: {
                id: id,
                withRemoting:"true",
                tabEntityName: entityName
            },
            dataType: 'html',
            beforeSend: function(jqXHR,settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function(jqXHR) {
                guiLoading.hide();
            },
            success: function(data) {
                guiLoading.hide();
                $('#'+entityName+"Div").html(data);

            }
        });
    }

    function renderInLineEdit(id) {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'editInLine')}',
            type: 'POST',
            data: {
                id: id,
                withRemoting:"true",
                tabEntityName:entityName
            },
            dataType: 'html',
            beforeSend: function(jqXHR,settings) {
                $('.alert.page').html('');
                guiLoading.show();

            },
            error: function(jqXHR) {
                guiLoading.hide();
            },
            success: function(data) {
                guiLoading.hide();
                $('#'+entityName+"Div").html(data);

            }
        });
    }
    function renderInLineCreate() {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'createInLine')}',
            type: 'POST',
            data: {
                'department.id': "${department.id}",
                'ownerDepartment.id': "${department.id}",
                'trainee.id': "${department.id}",
                tabEntityName:entityName,
                isDepartmentDisabled:true,
                isRelatedObjectTypeDisabled:true,
                isDocumentOwnerDisabled:true
            },
            dataType: 'html',
            beforeSend: function(jqXHR,settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            error: function(jqXHR) {
                guiLoading.hide();
            },
            success: function(data) {
                guiLoading.hide();
                $('#'+entityName+"Div").html(data);

            }
        });
    }

    function renderInLineList() {
        $('.alert.page').html('');
        guiLoading.show();
        $('a[href="#'+tabName+'"]').trigger("click");
    }

</script>

</body>
</html>