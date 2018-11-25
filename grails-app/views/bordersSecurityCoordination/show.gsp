<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'bordersSecurityCoordination.entity', default: 'Interview List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Interview List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'bordersSecurityCoordination', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>

<lay:simpleTab>

    <lay:simpleTabElement id="bordersSecurityCoordinationTab" entityName="bordersSecurityCoordination"
                          entityNameAction="show" title="${message(code: 'bordersSecurityCoordination.label')}"
                          icon="icon-commerical-building" isActive="true">
        <g:render template="/bordersSecurityCoordination/show"
                  model="[bordersSecurityCoordination: bordersSecurityCoordination]"/>

    </lay:simpleTabElement>

    <lay:simpleTabElement id="contactInfoTab" entityName="contactInfo"
                          commandName="ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand"
                          tabClassification="person"
                          title="${message(code: 'contactInfo.contacts.label', default: 'person contactInfo')}"
                          icon="icon-phone"/>


    <lay:simpleTabElement id="contactInfoAddressTab" entityName="contactInfo"
                          commandName="ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand"
                          tabClassification="person"
                          title="${message(code: 'contactInfo.addresses.label', default: 'person contactInfo')}"
                          icon="icon-address"/>

</lay:simpleTab>

<el:row/>

<script type="text/javascript">
    var entityName = "";
    var commandName = "";
    var tabName = "";
    var tabClassification = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "bordersSecurityCoordinationTab";
        var divId = $(this).attr("href");
        tabName = divId.trim().replace("#", "");

        $(".tab-content").find("div.tab-pane").each(function () {
            var id = $(this).attr('id');
            if (tabName != id && id != excludeTab) {
                $(this).html('');
            }
        });
        entityName = $(this).attr("entityName");
        commandName = $(this).attr("commandName");
        tabClassification = $(this).attr("tabClassification");
        var url = null;
        var holderEntityId = null;
        var holderEntityPath = null;
        var ids = [];
        if (tabName != excludeTab && tabName != "interviewTab") {
            if (tabClassification == "person") {
                url = "${createLink(controller: 'pcoreTabs',action: 'loadTab')}";
                holderEntityPath = "/pcore";
                holderEntityId = "${bordersSecurityCoordination?.employee?.personId}";
                <g:each in="${bordersSecurityCoordination?.contactInfos}" status="indx" var="array">
                ids.push("${array}");
                </g:each>

            } else {
                url = "${createLink(controller: 'tabs',action: 'loadTab')}";
                holderEntityId = "${bordersSecurityCoordination?.id}";
            }
            $.post(url, {
                tabName: tabName,
                holderEntityName: tabClassification,
                holderEntityPath: holderEntityPath,
                holderEntityId: holderEntityId,
                tabEntityName: entityName,
                commandName: commandName,
                "personId": "${bordersSecurityCoordination?.employee?.personId}",
                "ids[]": ids
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
            });
        } else {
            var id = "";
            if (tabClassification == "person") {
                url = "${createLink(controller: 'pcoreTabs',action: 'showInLine')}";
                id = "${bordersSecurityCoordination?.borderLocationId}";
            } else {
                url = "${createLink(controller: 'tabs',action: 'showInLine')}";
                id = "${bordersSecurityCoordination?.id}";
            }
            $.ajax({
                url: url,
                type: 'POST',
                data: {
                    id: id,
                    tabEntityName: entityName,
                    withRemoting: true
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
                    $(divId).html(data);

                }
            });
        }
    });

    function renderInLineShow(id) {
        var justAddress = null;
        var withRemoting = true;
        if (tabName == "contactInfoAddressTab") {
            justAddress = true;
        } else if (tabName == "contactInfoTab") {
            justAddress = false;
        }
        var url;
        if (tabClassification == "person") {
            url = "${createLink(controller: 'pcoreTabs',action: 'showInLine')}";
        } else {
            url = "${createLink(controller: 'tabs',action: 'showInLine')}";
        }
        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                tabEntityName: entityName,
                withRemoting: withRemoting,
                justAddress: justAddress
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

    function renderInLineEdit(id) {
        var justAddress = null;
        var withRemoting = true;


        if (tabName == "contactInfoAddressTab") {
            justAddress = true;
        } else if (tabName == "contactInfoTab") {
            justAddress = false;
        }


        var url;
        if (tabClassification == "person") {
            url = "${createLink(controller: 'pcoreTabs',action: 'editInLine')}";
        } else {
            url = "${createLink(controller: 'tabs',action: 'editInLine')}";
        }
        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                tabEntityName: entityName,
                isPersonDisabled: true,
                isEmployeeDisabled: true,
                isRelatedObjectTypeDisabled: true,
                justAddress: justAddress,
                isDocumentOwnerDisabled: true,
                withRemoting: withRemoting,
                isHiddenPersonInfo: true,
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
    function renderInLineCreate() {
        var url;
        var holderEntityId = null;
        var justAddress = null;
        var withEmployee = true;


        if (tabName == "contactInfoAddressTab") {
            justAddress = true;
        } else if (tabName == "contactInfoTab") {
            justAddress = false;
        }
        if (tabClassification == "person") {
            url = "${createLink(controller: 'pcoreTabs',action: 'createInLine')}";
            holderEntityId = "${bordersSecurityCoordination?.employee?.personId}";
        } else {
            url = "${createLink(controller: 'tabs',action: 'createInLine')}";
            holderEntityId = "${bordersSecurityCoordination?.id}";
        }

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                'person.id': "${bordersSecurityCoordination?.employee?.personId}",
                'employee.id': "${bordersSecurityCoordination?.employee?.id}",
                'ownerPerson.id': "${bordersSecurityCoordination?.employee?.personId}",
                'trainee.id': "${bordersSecurityCoordination?.employee?.personId}",
                tabEntityName: entityName,
                isPersonDisabled: true,
                isEmployeeDisabled: true,
                isRelatedObjectTypeDisabled: true,
                isDocumentOwnerDisabled: true,
                justAddress: justAddress,
                relatedObjectType: '${ps.police.pcore.enums.v1.ContactInfoClassification.PERSON}',
                documentOwner: '${ps.police.pcore.enums.v1.RelatedParty.PERSON}',
                withEmployee: withEmployee,
                isHiddenPersonInfo: true,
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

    function renderInLineList() {
        $('.alert.page').html('');
        guiLoading.show();
        $('a[href="#' + tabName + '"]').trigger("click");
    }

</script>

</body>
</html>

