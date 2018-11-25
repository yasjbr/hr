<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firm.entity', default: 'firm List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'firm List')}" />
    <title>${title}</title>
</head>

<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'firm', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<lay:simpleTab>
    <lay:simpleTabElement id="firmTab" entityName="firm" entityNameAction="show" title="${message(code: 'firm.label')}" icon="icon-commerical-building" isActive="true" >
        <g:render template="/firm/show" model="[firm: firm]"/>
    </lay:simpleTabElement>

    <lay:simpleTabElement id="firmSupportContactInfoTab"
                          entityName="firmSupportContactInfo" entityNameAction="list"
                          title="${message(code: 'firmSupportContactInfo.entities', default: 'firm firmSupportContactInfo')}"
                          icon="icon-firmSupportContactInfo">
    </lay:simpleTabElement>
</lay:simpleTab>

<script type="text/javascript">
    var entityName = "";
    var tabName = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "firmTab";
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
                holderEntityName: 'firm',
                holderEntityId: "${firm?.id}",
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
                    id: "${firm?.id}",
                    withRemotingValues:"true",
                    tabEntityName: "firm"
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
                isEncrypted: "true",
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
                isEncrypted: "true",
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
                'firm.id': "${firm.id}",
                'ownerfirm.id': "${firm.id}",
                'trainee.id': "${firm.id}",
                tabEntityName:entityName,
                isfirmDisabled:true,
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