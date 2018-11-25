<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'organization.entity', default: 'Organization List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Organization List')}" />
    <title>${title}</title>
</head>

<body>
<msg:page />
<lay:simpleTab>
    <lay:simpleTabElement id="organizationTab"
                          title="${message(code: 'organization.label')}"
                          icon="icon-commerical-building"
                          isActive="true" >
        <g:render template="/organization/show" model="[organization:organization]"/>
    </lay:simpleTabElement>

    <lay:simpleTabElement id="legalIdentifierTab"
                          entityName="legalIdentifier"
                          title="${message(code: 'legalIdentifier.entities', default: 'organization legalIdentifiers')}"
                          icon="icon-docs">
    </lay:simpleTabElement>

    <lay:simpleTabElement id="contactInfoTab"
                          entityName="contactInfo"
                          title="${message(code: 'contactInfo.entities', default: 'organization contactInfo')}"
                          icon="icon-phone">
    </lay:simpleTabElement>

    <lay:simpleTabElement id="organizationFocalPointTab"
                          entityName="organizationFocalPoint"
                          title="${message(code: 'organizationFocalPoint.entity', default: ' organizationFocalPoint')}"
                          icon="icon-users">
    </lay:simpleTabElement>
</lay:simpleTab>


<script type="text/javascript">
    var entityName = "";
    var tabName = "";
    $('a[data-toggle="tab"]').click(function () {

        $('.alert.page').html('');

        var excludeTab = "organizationTab";
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
                holderEntityName: 'organization',
                holderEntityId: "${organization?.id}",
                tabEntityName: entityName
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
            });
        }
        else{
            $.ajax({
                url: '${createLink(controller: 'tabs',action: 'showInLine')}',
                type: 'POST',
                data: {
                    id: "${organization?.id}",
                    tabEntityName: "organization"
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

    function renderInLineShow(id) {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'showInLine')}',
            type: 'POST',
            data: {
                id: id,
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
                tabEntityName:entityName,
                isOrganizationDisabled:true,
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
    function renderInLineCreate() {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'createInLine')}',
            type: 'POST',
            data: {
                'organization.id': "${organization.id}",
                'ownerOrganization.id': "${organization.id}",
                'relatedOrganization.id': "${organization.id}",
                tabEntityName:entityName,
                isOrganizationDisabled:true,
                isRelatedObjectTypeDisabled:true,
                isDocumentOwnerDisabled:true,
                relatedObjectType:'${ps.police.pcore.enums.v1.ContactInfoClassification.ORGANIZATION}',
                documentOwner:'${ps.police.pcore.enums.v1.RelatedParty.ORGANIZATION}'

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
        guiLoading.show();
        $('a[href="#'+tabName+'"]').trigger("click");
    }




    //script for focalPointContactInfo
    function listDetails(id){
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'listInLine')}',
            type: 'POST',
            data: {
                entityId: id,
                tabEntityName:"focalPointContactInfo"
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
                $('#organizationFocalPointDiv').html(data);
                gui.initAll.init($('#organizationFocalPointDiv'));
            }
        });
    }

    function addDetails(id){
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'createInLine')}',
            type: 'POST',
            data: {
                'organizationFocalPoint.id': id,
                tabEntityName:"focalPointContactInfo"
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
                $('#organizationFocalPointDiv').html(data);
                gui.initAll.init($('#organizationFocalPointDiv'));
            }
        });
    }

    function renderInLineShowDetails(id){
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'showInLine')}',
            type: 'POST',
            data: {
                id: id,
                tabEntityName:"focalPointContactInfo"
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
                $('#organizationFocalPointDiv').html(data);
                gui.initAll.init($('#organizationFocalPointDiv'));
            }
        });
    }


    function renderInLineEditDetails(id){
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'editInLine')}',
            type: 'POST',
            data: {
                id: id,
                tabEntityName:"focalPointContactInfo"
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
                $('#organizationFocalPointDiv').html(data);
                gui.initAll.init($('#organizationFocalPointDiv'));
            }
        });
    }

    //end script for focalPointContactInfo


</script>
</body>
</html>