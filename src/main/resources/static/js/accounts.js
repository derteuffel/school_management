$(document).ready(function() {
    $("#submit").attr("disabled", true);
    $("#update").show();
    $("#update").click(function() {
        $("#inputAvatar").toggle();
        $("#inputUsername").toggle();
        $("#inputEmail").toggle();
        $("#reset").show();
        $("#update").hide();
        $("#submit").attr("disabled", false);
    });


    $("#changePassword").click(function() {
        $("#inputHoldPasswor").toggle();
        $("#inputPassword").toggle();
        $("#update").show();
        $("#reset").hide();
        $("#submit").attr("disabled", false);
    });

    $("#reset").click(function(){
        $("#inputAvatar").toggle();
        $("#inputUsername").toggle();
        $("#inputEmail").toggle();
        $("#reset").hide();
        $("#update").show();
        $("#submit").attr("disabled", true);
    })
});