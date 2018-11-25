<script>

    /*in case: create/edit vacation configuration */
    function vacationTransferValueSettings(checkbox) {
        if (checkbox.checked == true) {
            gui.formValidatable.addRequiredField('vacationTransferValue');
            $('#vacationTransferValue').show();
        } else {
            gui.formValidatable.removeRequiredField('vacationTransferValue');
            $('#vacationTransferValue').hide();
        }
    }

    /*in case: edit vacation configuration & isTransferableToNewYear false*/
    $(document).ready(function () {
        if($("#isTransferableToNewYear").val()=='false'){
            gui.formValidatable.removeRequiredField('vacationTransferValue');
            $('#vacationTransferValue').hide();
        }
    })
</script>
