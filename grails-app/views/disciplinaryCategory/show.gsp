<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DisciplinaryCategory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'disciplinaryCategory', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<lay:simpleTab>
    <lay:simpleTabElement id="disciplinaryCategoryTab" entityName="disciplinaryCategory" entityNameAction="show" title="${message(code: 'disciplinaryCategory.label')}" icon="icon-commerical-building" isActive="true" >
        <g:render template="/disciplinaryCategory/show" model="[disciplinaryCategory: disciplinaryCategory]"/>
    </lay:simpleTabElement>
    <lay:simpleTabElement id="disciplinaryReasonTab"
                          entityName="disciplinaryCategoryReason" entityNameAction="list"
                          title="${message(code: 'disciplinaryReason.entities', default: 'disciplinary reason')}"
                          icon="icon-tasks">
    </lay:simpleTabElement>


</lay:simpleTab>


<script type="text/javascript">
    var entityName = "";
    var tabName = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "disciplinaryCategoryTab";
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
                holderEntityName: 'disciplinaryCategory',
                holderEntityId: "${disciplinaryCategory?.id}",
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
                    encodedId: "${disciplinaryCategory?.encodedId}",
                    tabEntityName: "disciplinaryCategory"
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
                'disciplinaryCategory.id': "${disciplinaryCategory.id}",
                'ownerdisciplinaryCategory.id': "${disciplinaryCategory.id}",
                'trainee.id': "${disciplinaryCategory.id}",
                tabEntityName:entityName,
                isdisciplinaryCategoryDisabled:true,
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