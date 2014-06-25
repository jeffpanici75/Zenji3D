<%
dim PageName(5)
PageName(0)="Home / News"
PageName(1)="Team"
PageName(2)="Screenshots"
PageName(3)="Downloads"
PageName(4)="Help"

page=Request.querystring("p")

select case page
Case 2
	CurrentPageName=PageName(1)
Case 3
	CurrentPageName=PageName(2)
Case 4
	CurrentPageName=PageName(3)
Case 5
	CurrentPageName=PageName(4)
Case else
	page=1
	CurrentPageName=PageName(0)
end select

page=cint(page)

' response.write "selected page =" & page & "<br>"
' response.write "current page name=" & CurrentPageName & "<br>"
%>
<html>

<head>
<meta http-equiv="Content-Language" content="en-us">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<LINK REL="STYLESHEET" TYPE="text/css" HREF="styles.css">
<title>Zenji 3d - <%=CurrentPageName%></title>
<SCRIPT TYPE="text/javascript">
<!--

function newImage(arg) {
	if (document.images) {
		rslt = new Image();
		rslt.src = arg;
		return rslt;
	}
}

function changeImages() {
	if (document.images && (preloadFlag == true)) {
		for (var i=0; i<changeImages.arguments.length; i+=2) {
			document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
		}
	}
}

var preloadFlag = false;
function preloadImages() {
	if (document.images) {
		Navigation_01_over = newImage("images/Navigation_01-over.gif");
		Navigation_02_over = newImage("images/Navigation_02-over.gif");
		Navigation_03_over = newImage("images/Navigation_03-over.gif");
		Navigation_04_over = newImage("images/Navigation_04-over.gif");
		Navigation_05_over = newImage("images/Navigation_05-over.gif");
		preloadFlag = true;
	}
}

// -->
</SCRIPT>
</head>

<body background="images/zbackground.jpg" bgproperties="fixed" ONLOAD="preloadImages();">

<div align=" center">
  <center>
  <table border="1" width="700" background="images/Backwithlogo.jpg" height="100%" bordercolor="#000080" bordercolorlight="#000066" bordercolordark="#FFFFFF" cellpadding="2" cellspacing="0">
    <tr>
      <td width="100%" colspan="2" valign="top">
        <p align="center"><img border="0" src="images/z_banner_1a.gif"></td>
    </tr>
    <tr>
      <td height="100%" valign="top">
<%' Navigation Start%>      
<TABLE WIDTH=130 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR><TD>
	<%if page<>1 then%>
			<A HREF="<%=request.servervariables("url")%>?p=1"
				ONMOUSEOVER="changeImages('Navigation_01', 'images/Navigation_01-over.gif'); return true;"
				ONMOUSEOUT="changeImages('Navigation_01', 'images/Navigation_01.gif'); return true;">
				<IMG NAME="Navigation_01" SRC="images/Navigation_01.gif" WIDTH=130 HEIGHT=58 BORDER=0 ALT="Home / News"></A>
	<%else%>
	<img src="images/Navigation_01-over.gif">
	<%end if%>
	</TD></TR>
	<TR><TD>
	<%if page<>2 then%>
			<A HREF="<%=request.servervariables("url")%>?p=2"
				ONMOUSEOVER="changeImages('Navigation_02', 'images/Navigation_02-over.gif'); return true;"
				ONMOUSEOUT="changeImages('Navigation_02', 'images/Navigation_02.gif'); return true;">
				<IMG NAME="Navigation_02" SRC="images/Navigation_02.gif" WIDTH=130 HEIGHT=57 BORDER=0 ALT="Team"></A>
	<%else%>
	<img src="images/Navigation_02-over.gif">
	<%end if%>
	</TD></TR>
	<TR><TD>
	<%if page<>3 then%>
			<A HREF="<%=request.servervariables("url")%>?p=3"
				ONMOUSEOVER="changeImages('Navigation_02', 'images/Navigation_02-Navigation_03.gif', 'Navigation_03', 'images/Navigation_03-over.gif'); return true;"
				ONMOUSEOUT="changeImages('Navigation_02', 'images/Navigation_02.gif', 'Navigation_03', 'images/Navigation_03.gif'); return true;">
				<IMG NAME="Navigation_03" SRC="images/Navigation_03.gif" WIDTH=130 HEIGHT=55 BORDER=0 ALT="Screenshots"></A>
	<%else%>
	<img src="images/Navigation_03-over.gif">
	<%end if%>
	</TD></TR>
	<TR><TD>
	<%if page<>4 then%>
			<A HREF="<%=request.servervariables("url")%>?p=4"
				ONMOUSEOVER="changeImages('Navigation_03', 'images/Navigation_03-Navigation_04.gif', 'Navigation_04', 'images/Navigation_04-over.gif'); return true;"
				ONMOUSEOUT="changeImages('Navigation_03', 'images/Navigation_03.gif', 'Navigation_04', 'images/Navigation_04.gif'); return true;">
				<IMG NAME="Navigation_04" SRC="images/Navigation_04.gif" WIDTH=130 HEIGHT=57 BORDER=0 ALT="Help"></A>
	<%else%>
	<img src="images/Navigation_04-over.gif">
	<%end if%>
	</TD></TR>
	<TR><TD>
	<%if page<>5 then%>
			<A HREF="<%=request.servervariables("url")%>?p=5"
				ONMOUSEOVER="changeImages('Navigation_04', 'images/Navigation_04-Navigation_05.gif', 'Navigation_05', 'images/Navigation_05-over.gif'); return true;"
				ONMOUSEOUT="changeImages('Navigation_04', 'images/Navigation_04.gif', 'Navigation_05', 'images/Navigation_05.gif'); return true;">
				<IMG NAME="Navigation_05" SRC="images/Navigation_05.gif" WIDTH=130 HEIGHT=55 BORDER=0 ALT=""></A>
	<%else%>
	<img src="images/Navigation_05-over.gif">
	<%end if%>
	</TD></TR>
</TABLE>
     
<%' Navigation End%>      
      </td>
      <td width="100%" valign="top">
        <table border="0" width="100%" cellspacing="0" cellpadding="0">
          <tr>
            <td width="50%" align="left"><p class="pageheader"><%=CurrentPageName%></p></td>
            <td width="50%" align="right"><img border="0" src="images/TopLogo.jpg"></td>
          </tr>
          <tr>
            <td width="100%" colspan="2">
            <%if Page=1 then%>
            <p><!--webbot bot="PurpleText"
            PREVIEW="Content for Page 1 goes below
" -->
            </p>
              <table border="1" width="100%" cellspacing="0" cellpadding="2">
                <tr>
                  <td width="50%" bgcolor="#000066"><b><font color="#FFFFFF">New Web Site!</font></b></td>
                  <td width="50%" bgcolor="#000066" align="right"><b><font color="#FFCC00">1/5/03</font></b></td>
                </tr>
                <tr>
                <td width="100%" bgcolor="#99CCFF" colspan="2">New web site, not
                  everything there yet, but getting close.</td>
                </tr>
                <tr>
                  <td width="50%" bgcolor="#000066"><b><font color="#FFFFFF">Beta 2 Available!</font></b></td>
                  <td width="50%" bgcolor="#000066" align="right"><b><font color="#FFCC00">12/31/02</font></b></td>
                </tr>
                <tr>
                <td width="50%" bgcolor="#99CCFF">It's the New Year's Eve build. Give it a Try.</td>
                <td width="50%" bgcolor="#99CCFF" align="right">[ <a href="<%=request.servervariables("url")%>?p=4"> Link</a> ]&nbsp;</td>
                </tr>
                <tr>
                  <td width="50%" bgcolor="#000066"><b><font color="#FFFFFF">Still
                    Alive!</font></b></td>
                  <td width="50%" bgcolor="#000066" align="right"><b><font color="#FFCC00">12/17/02</font></b></td>
                </tr>
                <tr>
                <td width="100%" colspan="2" bgcolor="#99CCFF">I am hard at work!!! Honest!  Seriously, things are moving along nicely and we're very close to beta...</td>
                </tr>
                                <tr>
                  <td width="50%" bgcolor="#000066"><b><font color="#FFFFFF">Sounds,
                    Tempo and Progress</font></b></td>
                  <td width="50%" bgcolor="#000066" align="right"><b><font color="#FFCC00">12/10/02</font></b></td>
                </tr>
                <tr>
                <td width="100%" colspan="2" bgcolor="#99CCFF">General
                  development update.&nbsp; I've been playing around with the
                  tempo issue as it relates to the game timer.&nbsp; Other
                  stuff...</td>
                </tr>
                                <tr>
                  <td width="50%" bgcolor="#000066"><b><font color="#FFFFFF">The
                    Decision is Made!</font></b></td>
                  <td width="50%" bgcolor="#000066" align="right"><b><font color="#FFCC00">12/09/02</font></b></td>
                </tr>
                <tr>
                <td width="100%" colspan="2" bgcolor="#99CCFF">It's official...Zenji
                  Face stays as-is!</td>
                </tr>
              </table>
            <%end if%> 
            <%if Page=2 then%>
            <p><!--webbot bot="PurpleText"
            PREVIEW="Content for Page 2 goes below
" -->
            <b>The Zenji 3D Team:</b></p>
              <table border="1" width="100%" cellspacing="0" cellpadding="2">
                <tr>
                  <td width="50%" bgcolor="#99CCFF">Programming</td>
                  <td width="50%" bgcolor="#99CCFF" align="right">Jeffrey D. Panici</td>
                </tr>
                <tr>
                  <td width="50%" bgcolor="#99CCFF">Artwork</td>
                  <td width="50%" bgcolor="#99CCFF" align="right">Edgar Ibarra (Feo)</td>
                </tr>
                <tr>
                  <td width="50%" bgcolor="#99CCFF">Sound/Music</td>
                  <td width="50%" bgcolor="#99CCFF" align="right">Aaron Ackerson</td>
                </tr>
                <tr>
                  <td width="50%" bgcolor="#99CCFF">Game Testing/Support</td>
                  <td width="50%" bgcolor="#99CCFF" align="right">Joshua Panici<br>
                    Joe Eberle</td>
                </tr>
              </table>
            <p>Massive thanks go to Matthew Hubbard for coming up with a truly fantastic
            game all those years ago!</p>
            <p>Kudos to Mark Sibly for bringing Blitz to the PC.  You've made more possible
            than you could possibly imagine!</p>
            <p><b><font color="#990033">About Zenji</font></b></p>
            <p>Let us call the "Many" the "Elements." Then let us see that in the
            vast blackness, beyond the beyond, the Elements assemble around the Source. Some are connected, some are not. Those united with the Source
            are green with energy, while the disconnected ones are an empty grey.<br>
            The Seeker must connect the Many to the One, thus turning all the Elements green with the power of the Source. In that instant, Zenji
            occurs.</p>
            <p>&nbsp;--  A Lecture from Rokan, Master of Zenji, 739 A.D.</p>
            <p>&nbsp;</p>
            <p><font color="#000080"><b>Comments from Matthew Hubbard on the history of the original
            Zenji:</b></font></p>
            <p>"I had just finished 'Dolphin', and I decided that I wanted to work on a bigger system, like&nbsp;
            the Atari 400 or 800 [the platform Zenji was originally released on]. I was always a puzzle fan,&nbsp;
            and at the time, maze games were very popular.  But I wanted to make a game that was&nbsp;
            different than simply cleaning up mazes."</p>
            <p>The idea came to him that it would be really cool if the player could change the maze.&nbsp;
            Thus, Zenji was born.</p>
            <p>"Some of the people who had seen the game as it was being developed wanted the player&nbsp;
            to have some kind of shooting capability." The decision not to have shooting ended up being
            one of logistics rather than preferences. According to Matt, "The problem was that joysticks&nbsp;
            at the time had only one button. So how can you use the joystick to turn the maze pieces AND&nbsp;
            shoot? There's just no easy way to do it." Asked about the title, Matt recalled: "Originally, I&nbsp;
            wanted to call it 'Rotating Heads',  which is a song by a group called 'English Beat'. That name&nbsp;
            got 'vetoed' by the Activision higher-ups, so the marketing department was left with the problem
            of  selling a nameless game. Then, during one closed-door brainstorming session which  I wasn't
            present at, one of the marketing people mentioned that the game was a very 'Zen-like' experience -&nbsp;
            there was no plan, and it was very chaotic." They finally settled on 'Zenji', which Matt tells&nbsp;
            us "is the same as a saint in the Christian religion, a deceased Zen master." It turns out Matt was&nbsp;
            not a fan of the name. His argument: "It's not Zen, it's operations research!"&nbsp;</p>
            <p>During the game's development, people were trying to come up with a 'story' behind it.
            "There didn't seem to be any reason for you to be in the middle of this maze trying to reconnect
            all the pieces. It was cool, but there wasn't any realistic point to it all. One thought was that you
            were a waiter and as you turned things around, tables would appear. That didn't really fly, so after
            the waiter idea we just got sick of trying to give it a sense of place and purpose. I said 'I'm going
            to make this a completely abstract game. If anyone can stick a realistic theme on it, I'll do it!"&nbsp;</p>
            <p>One of the high points in a game developer's career is having the opportunity to see people
            enjoying their game. Matt recalls one of his favorite anecdotes: "At the summer CES shows
            [Consumer Electronics Show], we always had a hospitality booth set up in which people could
            play Activision's newest releases. One day, as I was leaving the hotel, I saw somebody playing
            Zenji at the booth. I went out and had an evening,  and 5 hours later when I returned, the same
            person was sitting there. I asked him, 'Have you been playing all that time?' The person replied
            'All what time?', and looked at his watch. When he realized the amount of time he had spent at
            the game his jaw dropped as he said 'This game just took away 5 hours of my life....'"</p>
            <p>The difference between designing games now and then was that it wasn't marketing-driven.
            "People were allowed to put things out and see if it worked. Nowadays, [a game designer's] job
            is to repeat hits. At the time, there weren't as many sequels, so we did whatever was new and
            discovered what the hits were as we went."</p>
            <p>"As must be obvious, Zenji is completely programmer done. I did the art, music, everything."
            This was usually the case. The programmer had complete control over (and complete&nbsp;
            responsibility for!) every aspect of the game. "I do write music, so that&nbsp;
            wasn't completely out of my league, but the important thing was that gameplay was king."&nbsp;</p>
            <%end if%> 
            <%if Page=3 then%>
            <p><!--webbot bot="PurpleText"
            PREVIEW="Content for Page 3 goes below
" -->
            <b>New screenshots coming soon...</b></p>
            <%end if%> 
            <%if Page=4 then%>
            <p><!--webbot bot="PurpleText"
            PREVIEW="Content for Page 4 goes below
" -->
            </p>
              <table border="1" width="100%" cellspacing="0" cellpadding="2">
                <tr>
                  <td width="50%" bgcolor="#000066"><b><font color="#FFFFFF">Beta
                    2 Available!</font></b></td>
                  <td width="50%" bgcolor="#000066" align="right"><b><font color="#FFCC00">12/31/02</font></b></td>
                </tr>
                <tr>
                  <td width="100%" colspan="2" bgcolor="#99CCFF"><a href="http://zenji3d.workerbee.com/downloads/Zenji3D-Beta2.msi"><img border="0" src="images/download-button.gif" align="right"></a>It's
                    the New
                    Year's Eve build. Give it a Try.
                    <p>The previous build was a partial success.&nbsp; It
                    appears that a nasty compiler bug crept into my environment
                    during successive Blitz patches.&nbsp; The end result was
                    that the game would just crash during the loading phase on
                    some systems.&nbsp; This problem has been eliminated in this
                    build and the game should start assuming you have sufficient
                    system RAM and video RAM (about 8MB or so).</p>
                    <p>Through further testing I've come to the conclusion that
                    running Zenji 3D on any VooDoo 3x line of card isn't worth
                    your time.&nbsp; While the game itself runs fine, all of the
                    sprites are seriously flawed.&nbsp; This has to do with the
                    fact that the VooDoo 3x cards support aribtarily small
                    texture maps compared to newer hardware (it appears to be
                    256x256).&nbsp; I'm going to do a little more research on
                    this but 3Dfx is dead so I do not plan on officially
                    supporting these cards.</p>
                    <p>Various bugs have been fixed in this build and several
                    new assets have been added:</p>
                    <ul>
                      <li>A new high score name entry bitmap (thanks&nbsp;go to
                        Ed) and new high score music (thanks&nbsp;go to Aaron).
                      <li>Remixes of the level music and a new up-tempo version
                        from the composer and not my techno attempt at
                        increasing the frequency.
                      <li>It was possible to get <em>stuck</em> when trying to
                        grab a bonus if you rolled into the pipe at the last
                        second.&nbsp; This has been fixed by rolling the player
                        back in the previous direction if the pipe is about to
                        dissapear.&nbsp; Let me know if anyone still gets stuck.
                      <li>Due to a slight logic error the sparks weren't always
                        hitting their end pivot correctly.&nbsp; This should be
                        fixed now.
                      <li>The game over sequence is slightly modified to support
                        the new music and bitmap.&nbsp; When you die, you'll get
                        the Game Over model and the game over music.&nbsp; You
                        can press START (Enter) to skip this stage if you like.&nbsp;
                        If you have a new high score, you'll be brought to the
                        name entry bitmap while the high-score music plays.&nbsp;
                        When you're done here the high score table will come up
                        and then the splash sequence will <em>restart</em> at
                        the beginning.&nbsp; If you do <em>not</em> have a high
                        score you will be brought back to the beginning of the
                        attract sequence.
                      <li>There is a known timing problem between the end of the
                        game over/name entry/attract modes.&nbsp; I'm working on
                        a way to &quot;fake out&quot; my frame limiting timer to
                        take the load time of the attract mode into account so
                        this doesn't happen.
                      <li>If you play the game up to level 30 you'll get stuck.&nbsp;
                        This is due to an error in the puzzle generator.</li>
                    </ul>
                    <p>There are a few major changes in this version.&nbsp; All
                    new builds will be distrbuted as an MSI (Microsoft
                    Installer) package from now on.&nbsp; This means you may
                    need to download the Windows Installer/Uninstaller if you're
                    running Windows 95/98.&nbsp; Windows 2000 and XP users
                    should be OK.&nbsp; During the public beta I will provide an
                    MSI and an EXE bundled MSI with the installer built in.&nbsp;
                    This will triple the size!&nbsp; But I'll provide the option
                    for those users that want to download everything from one
                    place.</p>
                    <p>I've also started using PAK files for all resources.&nbsp;
                    As we near public beta I want to ensure we protect our
                    resources and the executable from potential hackers.&nbsp;
                    The PAK file is a RAR archive that is password protected and
                    encrypted.&nbsp; If you're on the team and want to extract
                    the file, e-mail me privately and I'll send you the
                    password.&nbsp; The executable is UPX'd to reduce its size
                    from approxiable 1.3MB to 500KB!&nbsp; What a difference!</p>
                    <p>Load times are a little slower due to uncompression/decryption
                    for RARs but all-in-all I'm satisfied this was the easiest
                    and most robust solution for our games.&nbsp; It only took
                    me about half a day to integrate the unrar.dll with Blitz
                    (via the beepak.dll).</p>
                    <p>Well, I'm sure there's more here that I'm forgetting.&nbsp;
                    However, this build should be very stable and work on all
                    systems except for the most low-end.&nbsp; If you have <em>any</em>
                    problems, please let me know ASAP.</p>
                    <p>I want to thank everyone again.&nbsp; I especially want
                    to thank Aaron who really pumped out the music this last
                    week and it all sounds absoultely fantastic!&nbsp; Everyone
                    has been quick to send me revisions and I truly appreciate
                    it.</p>
                    <p>Have a great New Year!</td>
                </tr>
              </table>
            <%end if%> 
            <%if Page=5 then%>
            <p><!--webbot bot="PurpleText"
            PREVIEW="Content for Page 5 goes below
" -->
            <b>
            Are you stuck???,</b></p>
            <p>Need some help installing?, or with something else?...</p>
            <%end if%>
              <p><!--webbot bot="PurpleText"
              PREVIEW="Whatever you place below goes everywhere" -->
            </p>
              <p>&nbsp;</td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td width="100%" colspan="2" valign="top">
        <table border="0" width="100%" cellspacing="0" cellpadding="0">
          <tr>
            <td width="125%" align="center" colspan="5"><br>
            <%for i=0 to 4%>
              [ <%if i+1<>page then%><a href="<%=request.servervariables("url")%>?p=<%=i+1%>"><%end if%><b><%=PageName(i)%></b><%if i+1<>page then%></a><%end if%> ]
            <%next%><br>
              <br>
            </td>
          </tr>
          <tr>
            <td width="25%" align="center" height="85"><img border="0" src="images/platform_yell.gif"></td>
            <td width="25%" align="center"><img border="0" src="images/platform_blue.gif"></td>
            <td width="25%" align="center"><img border="0" src="images/platform_yell.gif"></td>
            <td width="25%" align="center"><img border="0" src="images/platform_blue.gif"></td>
            <td width="25%" align="center"><img border="0" src="images/platform_yell.gif"></td>
          </tr>
        </table>
        <p align="center"><font size="1" color="#000066"><b>(c) 2003 Workerbee
        Entertainment Inc.</b></font></p>
      </td>
    </tr>
  </table>
  </center>

</body>

</html>




