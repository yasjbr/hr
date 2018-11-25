<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNotice.entity', default: 'LoanNotice List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanNotice List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'loanNotice', action: 'list')}'"/>
    </div></div>
</div>

<el:row/>
<lay:simpleTab>
    <lay:simpleTabElement id="loanNoticeTab" entityName="loanNotice" entityNameAction="show"
                          title="${message(code: 'loanNotice.entity')}"
                          isActive="true">
        <g:render template="/loanNotice/show" model="[loanNotice: loanNotice]"/>
    </lay:simpleTabElement>


    <lay:simpleTabElement id="loanNoticeReplayRequestTab"
                          entityName="loanNoticeReplayRequest" entityNameAction="list"
                          title="${message(code: 'loanNoticeReplayRequest.entities', default: 'loanNoticeReplayRequest')}"
                          icon="icon-th-4">
    </lay:simpleTabElement>

</lay:simpleTab>

<script type="text/javascript">
    var entityName = "";
    var tabName = "";
    $('a[data-toggle="tab"]').click(function () {

        $('.alert.page').html('');

        var excludeTab = "loanNoticeTab";
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
            var loadHolderEntityInformation = false;
            if(entityName == "loanNoticeReplayRequest"){
                loadHolderEntityInformation = true;
            }

            $.post("${createLink(controller: 'tabs',action: 'loadTab')}", {
                tabName: tabName,
                tabEntityName: entityName,
                holderEntityName: 'loanNotice',
                holderEntityId: "${loanNotice?.encodedId}",
                loadHolderEntityInformation:loadHolderEntityInformation,
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                $('.chosen-select', $(divId)).chosen({allow_single_deselect: true,placeholder_text_single: "${message(code:'default.choose.label')}",placeholder_text_multiple:"${message(code:'default.choose.label')}"});
                gui.dataTable.initialize($(divId));
                gui.dateTimePickers.initialize($(divId));
                gui.autocomplete.initialize($(divId));
                gui.modal.initialize($(divId));
                gui.inputs.initialize($(divId));
            });
        }else{
            $.ajax({
                url: '${createLink(controller: 'tabs',action: 'showInLine')}',
                type: 'POST',
                data: {
                    encodedId: "${loanNotice?.encodedId}",
                    tabEntityName: "loanNotice",
                    withRemoting:true,
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

    function renderInLineShow(encodedId) {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'showInLine')}',
            type: 'POST',
            data: {
                encodedId: encodedId,
                tabEntityName:entityName,
                withRemoting:true,

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
                $("#"+entityName+"Div").html(data);
                gui.initAll.init($('#'+entityName+'Div'));
            }
        });
    }

    function renderInLineEdit(encodedId) {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'editInLine')}',
            type: 'POST',
            data: {
                encodedId: encodedId,
                tabEntityName:entityName,
                withRemoting:true,
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
                $("#"+entityName+"Div").html(data);
                gui.initAll.init($('#'+entityName+'Div'));
            }
        });
    }
    function renderInLineCreate() {
        $.ajax({
            url: '${createLink(controller: 'tabs',action: 'createInLine')}',
            type: 'POST',
            data: {
                'loanNotice.encodedId': "${loanNotice.encodedId}",
                tabEntityName:entityName,
                withRemoting:true,
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
                $("#"+entityName+"Div").html(data);
                gui.initAll.init($('#'+entityName+'Div'));
            }
        });
    }

    function renderInLineList() {
        guiLoading.show();
        $('a[href="#'+tabName+'"]').trigger("click");
    }

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

</script>
</body>
</html>