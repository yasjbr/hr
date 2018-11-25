<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'Applicant List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Applicant List')}"/>
    <title>${title}</title>
</head>

<body>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'applicant', action: 'list')}'"/>
    </div></div>
</div>

<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${applicant?.personName}" type="String"
                     label="${message(code: 'applicant.personName.label', default: 'personName')}"/>
    <lay:showElement value="${applicant?.applicantCurrentStatus?.applicantStatus}" type="enum"
                     label="${message(code: 'applicant.applicantCurrentStatus.label', default: 'applicantCurrentStatus')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${applicant?.applyingDate}" type="ZonedDate"
                     label="${message(code: 'applicant.applyingDate.label', default: 'applyingDate')}"/>

    <lay:showElement value="${applicant?.id}" type="String"
                     label="${message(code: 'applicant.applicantNumber.label', default: 'applicantNumber')}"/>
</lay:showWidget>
<el:row/>
<br/>
<lay:simpleTab>
    <lay:simpleTabElement id="applicantTab" entityName="applicant" entityNameAction="show"
                          title="${message(code: 'applicant.entity')}"
                          icon="icon-user" tabClassification="applicant"
                          isActive="true">
        <g:render template="/applicant/show" model="[applicant: applicant]"/>
    </lay:simpleTabElement>


    <lay:simpleTabElement id="inspectionCategoryTab" tabClassification="applicant"
                          entityName="applicantInspectionCategoryResult" entityNameAction="list"
                          title="${message(code: 'applicantInspectionCategoryResult.entities', default: 'applicant inspection')}"
                          icon="icon-th-4">
    </lay:simpleTabElement>



    <lay:simpleTabElement id="interviewTab" tabClassification="applicant"
                          entityName="interview" entityNameAction="show"
                          title="${message(code: 'interview.entity', default: 'interview')}"
                          icon="icon-users-3">
    </lay:simpleTabElement>

    <lay:simpleTabElement id="traineeListEmployeeTab" tabClassification="applicant"
                          entityName="traineeListEmployee" entityNameAction="show"
                          title="${message(code: 'traineeListEmployee.result.label', default: 'TraineeList')}"
                          icon="icon-tools">
    </lay:simpleTabElement>

    <lay:simpleTabElement id="applicantStatusHistoryTab" tabClassification="applicant"
                          entityName="applicantStatusHistory" entityNameAction="list"
                          title="${message(code: 'applicantStatusHistory.entities', default: 'ApplicantStatusHistory')}"
                          icon="icon-statusnet">
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

    <lay:simpleTabElement id="personEducationTab" entityName="personEducation"
                          commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonEducationCommand"
                          tabClassification="person"
                          title="${message(code: 'personEducation.entities', default: 'person education')}"
                          icon="icon-graduation-cap"/>


    <lay:simpleTabElement id="personEmploymentHistoryTab" entityName="personEmploymentHistory"
                          commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonEmploymentHistoryCommand"
                          tabClassification="person"
                          title="${message(code: 'personEmploymentHistory.entities', default: 'personEmploymentHistory')}"
                          icon="icon-suitcase"/>

    <lay:simpleTabElement id="personArrestHistoryTab" entityName="personArrestHistory"
                          commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonArrestHistoryCommand"
                          tabClassification="person"
                          title="${message(code: 'personArrestHistory.entities', default: 'personArrestHistory')}"
                          icon="icon-home-4"/>


    <lay:simpleTabElement id="personHealthHistoryTab" entityName="personHealthHistory"
                          commandName="ps.police.pcore.v2.entity.person.commands.v1.PersonHealthHistoryCommand"
                          tabClassification="person"
                          title="${message(code: 'personHealthHistory.entities', default: 'person Health History')}"
                          icon="icon-heart"/>

</lay:simpleTab>

<script type="text/javascript">
    var entityName = "";
    var commandName = "";
    var tabName = "";
    var tabClassification = "";
    $('a[data-toggle="tab"]').click(function () {
        $('.alert.page').html('');
        var excludeTab = "applicantTab";
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

        if (tabName != excludeTab && tabName != "interviewTab" && tabName != "traineeListEmployeeTab") {
            if (tabClassification == "person") {
                url = "${createLink(controller: 'pcoreTabs',action: 'loadTab')}";
                holderEntityPath = "/pcore";
                holderEntityId = "${applicant?.personId}";
            } else {
                url = "${createLink(controller: 'tabs',action: 'loadTab')}";
                holderEntityId = "${applicant?.id}";
            }
            $.post(url, {
                tabName: tabName,
                holderEntityName: tabClassification,
                holderEntityPath: holderEntityPath,
                holderEntityId: holderEntityId,
                tabEntityName: entityName,
                commandName: commandName
            }, function (data) {
                $(divId).html(data);
                guiLoading.hide();
                gui.dataTable.initialize($(divId));
                gui.modal.initialize($(divId));
            });
        } else {
            var id = "";
            var isInterview = false;
            var isTraineeListEmployee = false;
            if (tabClassification == "person") {
                url = "${createLink(controller: 'pcoreTabs',action: 'showInLine')}";
                id = "${applicant?.personId}";
            } else {
                url = "${createLink(controller: 'tabs',action: 'showInLine')}";
                id = "${applicant?.id}";
            }

            if (tabName == "interviewTab") {
                isInterview = true;
            }
            else if (tabName == "traineeListEmployeeTab") {
                isTraineeListEmployee = true;
            } else {
                isInterview = false;
                isTraineeListEmployee = false
            }
            $.ajax({
                url: url,
                type: 'POST',
                data: {
                    id: id,
                    tabEntityName: tabClassification,
                    withRemoting: true,
                    isInterview: isInterview,
                    isTraineeListEmployee: isTraineeListEmployee
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

        if (tabName == "inspectionCategoryTab") {
            withRemoting = false;
        }
        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id: id,
                encodedId: id,
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

        if (tabName == 'inspectionCategoryTab') {
            withRemoting = false;
        }


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
                encodedId: id,
                tabEntityName: entityName,
                isPersonDisabled: true,
                isEmployeeDisabled: true,
                isRelatedObjectTypeDisabled: true,
                justAddress: justAddress,
                isDocumentOwnerDisabled: true,
                withRemoting: withRemoting
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

        /*set withEmployee false for inspection category tab*/
        if (tabName == "inspectionCategoryTab") {
            withEmployee = false;
        }

        if (tabName == "contactInfoAddressTab") {
            justAddress = true;
        } else if (tabName == "contactInfoTab") {
            justAddress = false;
        }
        if (tabClassification == "person") {
            url = "${createLink(controller: 'pcoreTabs',action: 'createInLine')}";
            holderEntityId = "${applicant?.personId}";
        } else {
            url = "${createLink(controller: 'tabs',action: 'createInLine')}";
            holderEntityId = "${applicant?.id}";
        }

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                'person.id': holderEntityId,
                'employee.id': holderEntityId,
                'ownerPerson.id': holderEntityId,
                'trainee.id': holderEntityId,
                tabEntityName: entityName,
                isPersonDisabled: true,
                isEmployeeDisabled: true,
                isRelatedObjectTypeDisabled: true,
                isDocumentOwnerDisabled: true,
                justAddress: justAddress,
                relatedObjectType: '${ps.police.pcore.enums.v1.ContactInfoClassification.PERSON}',
                documentOwner: '${ps.police.pcore.enums.v1.RelatedParty.PERSON}',
                withEmployee: withEmployee
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