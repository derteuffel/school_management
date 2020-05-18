const joinButton = document.getElementById('join')
const joinButtonAudio = document.getElementById('joinAudio')
const join = async ()=>{
    $('#body').preloader()
    await VoxeetSDK.session.open({name:document.getElementById('username').value})
    let conference=null
    try {
        conference = await VoxeetSDK.conference.fetch(document.getElementById('conferenceId').value)
    }
    catch(e) {
        $('#body').preloader('remove')
        $('#joinError').css('display','block')
        console.log('cannot get the conference',e)
    }
    if(!conference){

        $('#body').preloader('remove')
        $('#joinError').css('display','block')
        VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
    }
    else
    {
        VoxeetSDK.conference.join(conference,{audio:true,video:true}).then(()=>{
            $('#body').preloader('remove')
            document.getElementById('video-super-container').style.display='flex'
            VoxeetSDK.conference.startVideo(VoxeetSDK.session.participant).catch(error => {
                console.log(error)
            })

        }  ).catch(e=>{
            $('#body').preloader('remove')
            $('#joinError').css('display','block')
            console.log('error cannot join',e)
            VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
        })
    }
}
const joinAudio = async ()=>{
    $('#body').preloader()
    await VoxeetSDK.session.open({name:document.getElementById('username').value})
    let conference=null
    try {
        conference = await VoxeetSDK.conference.fetch(document.getElementById('conferenceId').value)
    }
    catch(e) {
        $('#body').preloader('remove')
        $('#joinError').css('display','block')
        console.log('cannot get the conference',e)
    }
    if(!conference){

        $('#joinError').css('display','block')
        $('#body').preloader('remove')

    }
    else
    {
        VoxeetSDK.conference.join(conference,{audio:true,video:false}).then(()=>{
            interval()
            $('#body').preloader('remove')
            document.getElementById('audioComponent').style.display='flex'
        }  ).catch(e=>{
            $('#joinError').css('display','block')
            $('#body').preloader('remove')
            console.log('error',e)

        })
    }
}
joinButton.addEventListener('click',join)
joinButtonAudio.addEventListener('click',joinAudio)