<!--
*
* MIFXML to XHTML
* Version 2005-03-29 15:23
* Joel Håkansson, TPB
*
* Bilder hanteras inte
* viss text upprepas (varning finns i utfilen)
* Tabellhuvud och tabellfot kommer med, men saknar särskild uppmärkning
* Cellspan/Rowspan hanteras inte
* PgfNumStr kommer inte alltid med (p.g.a fulfix)
* Noter: Numreringen nollställs efter varje fil. Texten kommer på plats, direkt efter referensen.
* (Markers saknas)
* Wrappade P-taggar, är inget problem vid export till Word, men kan innebära problem vid konvertering till DTBook
* Vissa p-taggar har style-attribut (Ej tillåtet i XHTML)
* 
* Ändringar sedan förra versionen:
* TextRect innuti AFrames hanteras bättre
* Uppsnyggning av vissa delar i koden
* Experimenterar med keys
* 
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:key name="taggedParas" match="/MIFXML/TextFlow/Para" use="ParaLine/TextRectID/@attr1"/>
  <xsl:key name="tables" match="/MIFXML/Tbls/Tbl" use="TblID/@attr1"/>
  <xsl:key name="frames" match="/MIFXML/AFrames/Frame" use="ID/@attr1"/>
  
   <xsl:template match="/">
      <html>
         <head>
            <title />
         </head>
         <body>
            <p class="meta">Skapad med MIFXML to XHTML<br />
            Version 2005-03-29 15:23</p>
            <xsl:apply-templates select="." mode="part" />
         </body>
      </html>
   </xsl:template>

   <xsl:template match="/" mode="part">
<!-- Avoids multiple <html><head>...-tags -->
      <xsl:choose>
<!-- Book or document? -->
         <xsl:when test="count(*/MIFFile)&gt;0">
<!-- Mif-file -->
            <xsl:call-template name="file" />
         </xsl:when>
         <xsl:when test="count(*/Book)&gt;0">
<!-- Mif-Book -->
            <xsl:call-template name="book" />
         </xsl:when>
         <xsl:otherwise>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template name="book">
      <xsl:for-each select="*/BookComponent">
         <xsl:message>
            <xsl:value-of select="concat(translate(./FileName/@attr1, ' ','_'),'.mif.xml')" />
         </xsl:message>
      </xsl:for-each>
      <xsl:call-template name="debug">
         <xsl:with-param name="value">Start</xsl:with-param>
      </xsl:call-template>
      <xsl:for-each select="*/BookComponent">
         <xsl:call-template name="debug">
            <xsl:with-param name="value">
               <xsl:value-of select="concat(translate(./FileName/@attr1, ' ','_'),'.mif.xml')" />
            </xsl:with-param>
         </xsl:call-template>
         <div class="{./FileName/@attr1}">
            <xsl:apply-templates select="document(concat(translate(substring-after(./FileName/@attr1, '&lt;c&gt;'), ' ','_'),'.mif.xml'),/)" mode="part" />
         </div>
      </xsl:for-each>
      <xsl:call-template name="debug">
         <xsl:with-param name="value">Slut</xsl:with-param>
      </xsl:call-template>
   </xsl:template>

<!-- ska tas bort så småningom -->
   <xsl:template name="debug">
   <xsl:param name="value" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <p class="debug" style="color: #FF0000; background-color: #A0A0A0">
      <xsl:value-of select="$value" />
   </p>
   </xsl:template>
   
   <xsl:template name="warning">
   <xsl:param name="value" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <p class="warning" style="color: #000000; background-color: #FF20FF">
      <xsl:value-of select="$value" />
   </p>
   </xsl:template>

   <xsl:template name="file">
      <xsl:for-each select="MIFXML/Page">
         <xsl:if test="./PageType/@attr1='BodyPage'">
            <xsl:variable name="pn" select="./PageNum/@attr1" />
            <xsl:variable name="firstTRID" select="generate-id(./TextRect[1])"/>
            <xsl:for-each select="child::TextRect">
<!-- Den här choose-satsen gör inget just nu, men kan vara intressant senare, när man väljer flera barn förutom TextRect -->
               <xsl:choose>
                  <xsl:when test="self::TextRect">
                    <!-- Hantera sidor utan textinnehåll -->
                    <xsl:variable name="TFID" select="./ID/@attr1"/>
                    <xsl:if test="count(key('taggedParas', $TFID))=0">
                      <p>
                        <xsl:call-template name="FormatPageNum">
                          <xsl:with-param name="pn" select="$pn"/>
                        </xsl:call-template>
                      </p>
                    </xsl:if>
                    <xsl:apply-templates select="." mode="pre">
                      <xsl:with-param name="pn" select="$pn"/>
                      <xsl:with-param name="firstTRID" select="$firstTRID"/>
                    </xsl:apply-templates>
                  </xsl:when>
                  <xsl:otherwise />
               </xsl:choose>
               
            </xsl:for-each>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>
  
  <xsl:template match="TextRect" mode="pre">
    <xsl:param name="pn"/>
    <xsl:param name="firstTRID"/>
    
    <xsl:variable name="TFID" select="./ID/@attr1"/>
    <xsl:variable name="currentTRID" select="generate-id(.)"/>
    <xsl:variable name="taggedParas" select="key('taggedParas', $TFID)"/>
    
    <!--/MIFXML/TextFlow/Para[ParaLine/TextRectID/@attr1=$TFID]"-->
    <xsl:variable name="firstID" select="generate-id(key('taggedParas', $TFID)[1])"/>
  
    <!-- För varje Para som har TextRectID=$TFID -->
    <xsl:for-each select="$taggedParas">                 
      <!-- Om det här är första stycket på en ny sida, så ge variablen pnOk rätt nummer -->
      <xsl:variable name="pnOk">
        <xsl:choose>
          <xsl:when test="generate-id(current())=$firstID and $currentTRID=$firstTRID"><xsl:value-of select="$pn"/></xsl:when>
          <xsl:otherwise/>
        </xsl:choose>
      </xsl:variable>
      <!-- Det blir problem när ett stycke innehåller flera textrectId med olika värden eftersom 
           kontrollen sker på styckenivå. Detta är inte så vanligt, men kan förekomma. Just nu markeras detta endast för
           manuell kontroll. -->
      <xsl:choose>
        <!-- Det finns bara ett ID eller alla IDn är lika. -->
        <xsl:when test="count(./ParaLine/TextRectID)=count(./ParaLine/TextRectID[@attr1=$TFID])">
          <xsl:apply-templates select=".">
            <xsl:with-param name="pn" select="$pnOk"/>
            <xsl:with-param name="TFID" select="$TFID"/>
          </xsl:apply-templates>
          <!-- Välj alla följande Para (som inte själva har ett eget TextRectID) 
               som har den här Para som (närmsta föregående granne med ett TextRectID) -->
          <xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                                (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>
        </xsl:when>
        <!-- Det finns flera IDn och de är olika. -->
        <!-- Är detta det första stycket med detta id? -->
        <xsl:when test="generate-id(current())=generate-id($taggedParas[1])">
          <xsl:choose>
            <!-- Är detta det sista ID:t i stycket? -->
            <xsl:when test="generate-id(./ParaLine/TextRectID[last()])=generate-id(./ParaLine/TextRectID[@attr1=$TFID][last()])">
              <xsl:call-template name="warning">
                <xsl:with-param name="value">I följande stycke finns flera sidbrytningar, varav endast den första är utmärkt.</xsl:with-param>
              </xsl:call-template>
              <xsl:apply-templates select="."><xsl:with-param name="pn" select="$pnOk" /><xsl:with-param name="TFID" select="$TFID"/></xsl:apply-templates>
              <xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                              (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>
            </xsl:when>
            <!-- Om inte, så behandla detta stycke särskilt -->
            <xsl:otherwise>
              <!-- Men efterföljande stycken som vanligt -->
              <!--<xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                              (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>      -->
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <!-- Det finns föregående stycken med detta id. Lämna obehandlat -->
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="Para" mode="special">
  <xsl:param name="pn">0</xsl:param>
  <xsl:param name="TFID"/>
        <xsl:call-template name="warning">
        <xsl:with-param name="value">SPECIAL BEGIN ---</xsl:with-param>
     </xsl:call-template>
  <p class="{translate(PgfTag/@attr1, ' .&lt;&gt;+=', '')}">
<!-- behandla noder före första fontnoden -->
<!--  <span style="background-color: #ff80ff"> -->
        <xsl:apply-templates select="ParaLine/*[count(../preceding-sibling::ParaLine/Font)+count(preceding-sibling::Font)=0]">
           <xsl:with-param name="pn" select="$pn" />
           <xsl:with-param name="TFID" select="$TFID"/>
        </xsl:apply-templates>
<!--  </span> -->
<!-- behandla alla fontnoder (och innehållet som följer fram till nästa fontnod)-->
     <xsl:apply-templates select="ParaLine/Font" mode="ft">
        <xsl:with-param name="pn" select="$pn" />
        <xsl:with-param name="TFID" select="$TFID"/>
     </xsl:apply-templates>
  </p>
        <xsl:call-template name="warning">
        <xsl:with-param name="value">--- SPECIAL END</xsl:with-param>
     </xsl:call-template>   
  </xsl:template>
  
<!-- Används troligtvis inte -->
   <xsl:template name="style">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:attribute name="style">background-color: #c0c0c0; border-style: solid; border-width: 1px; border-color: #ff0000; margin: 1px;</xsl:attribute>
   </xsl:template>

<!-- välj alla barn till den här nodens förälders följande ParaLine-syskon som har den här fontnoden som sin närmaste föregående förälders syskonbarn-->
<!-- generate-id(parent::preceding-sibling::ParaLine::Font[last()]) = generate-id(current()) -->
<!-- parent::following-sibling::ParaLine::child::following-sibling[1] -->
   <xsl:template match="Font" mode="ft">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:variable name="Current" select="." />
<!-- Om teckenformatet finns i katalogen så välj den noden istället, annars välj aktuell nod -->
<!-- Hanterar med andra ord inte överlagring av namngivna teckenformat -->
   <xsl:variable name="FontInfo" select="//FontCatalog/Font[FTag/@attr1=$Current/FTag/@attr1]|$Current[not(string(FTag/@attr1))]" />
   <xsl:choose>
<!-- Sparar teckenformatsnamnet i ett klass-attribut men översätter ändå vanliga teckenformat -->
      <xsl:when test="string($FontInfo/FTag/@attr1)">
         <span class="{$FontInfo/FTag/@attr1}">
            <xsl:call-template name="FontTests-Start">
               <xsl:with-param name="Current" select="$FontInfo" />
               <xsl:with-param name="pn" select="$pn" />
               <xsl:with-param name="TFID" select="$TFID"/>
            </xsl:call-template>
         </span>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-Start">
            <xsl:with-param name="Current" select="$FontInfo" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-Final">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:call-template name="getTextNodes">
      <xsl:with-param name="pn" select="$pn" />
      <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:call-template>
   </xsl:template>

   <xsl:template name="FontTests-03">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FWeight/@attr1='Bold' or $Current/FWeight/@attr1='SemiBold'">
         <strong>
            <xsl:call-template name="FontTests-Final">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="TFID" select="$TFID"/>
               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </strong>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-Final">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-02">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FPosition/@attr1='FSubscript'">
         <sub>
            <xsl:call-template name="FontTests-03">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="pn" select="$pn" />
               <xsl:with-param name="TFID" select="$TFID"/>
            </xsl:call-template>
         </sub>
      </xsl:when>

      <xsl:otherwise>
         <xsl:call-template name="FontTests-03">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-01">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FPosition/@attr1='FSuperscript'">
         <sup>
            <xsl:call-template name="FontTests-02">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="TFID" select="$TFID"/>

               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </sup>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-02">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-Start">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FAngle/@attr1='Italic'">
         <em>
            <xsl:call-template name="FontTests-01">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="pn" select="$pn" />
               <xsl:with-param name="TFID" select="$TFID"/>
            </xsl:call-template>
         </em>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-01">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="getTextNodes">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
<!-- välj alla följande syskon som har den här fontnoden som närmaste föregående syskon -->
   <xsl:apply-templates select="following-sibling::*[generate-id(preceding-sibling::Font[1]) = generate-id(current())]">
      <xsl:with-param name="pn" select="$pn" />
      <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:apply-templates>
<!-- Dont ask, it works ;) -->
   <xsl:apply-templates select=" ../following-sibling::ParaLine/*[generate-id(((../preceding-sibling::ParaLine/Font)|(preceding-sibling::Font))[last()]) = generate-id(current()) ]">
      <xsl:with-param name="pn" select="$pn" />
      <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="Para">
   <xsl:param name="pn">0</xsl:param>
   <xsl:param name="TFID"/>
   <p class="{translate(PgfTag/@attr1, ' .&lt;&gt;+=', '')}">
<!-- behandla noder före första fontnoden -->
<!--  <span style="background-color: #ff80ff"> -->
         <xsl:apply-templates select="ParaLine/*[count(../preceding-sibling::ParaLine/Font)+count(preceding-sibling::Font)=0]">
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:apply-templates>
<!--  </span> -->
<!-- behandla alla fontnoder (och innehållet som följer fram till nästa fontnod)-->
      <xsl:apply-templates select="ParaLine/Font" mode="ft">
         <xsl:with-param name="pn" select="$pn" />
         <xsl:with-param name="TFID" select="$TFID"/>
      </xsl:apply-templates>
   </p>
   </xsl:template>

   <xsl:template name="FormatPageNum">
   <xsl:param name="pn">0</xsl:param>
   <xsl:param name="TFID"/>
   <xsl:if test="$pn &gt; 0">
      <span id="p-{$pn}" class="pagenum-normal" style="background-color: #d0a040">
         <xsl:value-of select="$pn" />
      </span>
   </xsl:if>
   </xsl:template>
   
   <xsl:template match="TextRectID">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:if test="$TFID=@attr1">
   <xsl:call-template name="FormatPageNum">
     <xsl:with-param name="pn" select="$pn"/>
     <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:call-template>
   </xsl:if>
   </xsl:template>

   <xsl:template match="Row" mode="table">
         <xsl:for-each select=".">
         <tr>
            <xsl:for-each select="./Cell">
               <td>
                  <xsl:apply-templates select="CellContent/Para">
                     <!-- <xsl:with-param name="pn" select="$pn" /> -->
                  </xsl:apply-templates>
               </td>
            </xsl:for-each>
         </tr>
      </xsl:for-each>
   </xsl:template>
   
   <xsl:template match="ATbl">
   <!-- <xsl:param name="pn" /> -->
   <xsl:variable name="id" select="@attr1" />
   <xsl:apply-templates select="key('tables', $id)/TblTitle/TblTitleContent/Para"/>
   <table border="1">
      <xsl:apply-templates select="key('tables', $id)/TblH/Row" mode="table"/>
      <xsl:apply-templates select="key('tables', $id)/TblBody/Row" mode="table"/>
      <xsl:apply-templates select="key('tables', $id)/TblF/Row" mode="table"/>
   </table>
   </xsl:template>

   <xsl:template match="AFrame">
   <!-- <xsl:param name="pn" /> -->
   <xsl:variable name="id" select="@attr1" />
   <xsl:for-each select="key('frames', $id)/*">
      <xsl:choose>
         <xsl:when test="self::TextRect">
         <!--
            <xsl:variable name="id2" select="./ID/@attr1" />
            <xsl:variable name="fp2" select="/MIFXML/TextFlow/Para[ParaLine/TextRectID/@attr1=$id2]" />
            <xsl:apply-templates select="$fp2[1]">
               <xsl:with-param name="TFID" select="$id2" />-->
               <!-- <xsl:with-param name="pn" select="$pn" /> -->
               <!-- <xsl:with-param name="TRNext" select="0" />
            </xsl:apply-templates> -->
            <!-- ful-lösning för att fixa paras i AFrame, kolla upp -->
            <!-- <xsl:for-each select="$fp2[1]">
            <xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                               (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>
            </xsl:for-each>-->
            <!-- Ska ersätta det som står ovanför genom modularisering -->
            <xsl:apply-templates select="." mode="pre"/>
            
         </xsl:when>
         <xsl:when test="self::ImportObject">
            <span class="NotIncluded">(OBJECT:Embedded)</span>
         </xsl:when>
         <xsl:when test="self::Math">
            <span class="NotIncluded">(OBJECT:Math)</span>
         </xsl:when>
         <xsl:otherwise />
      </xsl:choose>
   </xsl:for-each>
   </xsl:template>

   <xsl:template match="*">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:value-of select="." />
   </xsl:template>

   <xsl:template match="String">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:if test="@attr1 = ../../ParaLine[1]/String[1]/@attr1">
      <xsl:value-of select="../../PgfNumString/@attr1" />
   </xsl:if>
   <xsl:value-of select="@attr1" />
   </xsl:template>
   
   <xsl:template match="FNote">
     <xsl:variable name="refid" select="@attr1"/>
     <xsl:variable name="number" select="count(preceding::FNote[not(parent::Notes)])+1"/>
     <span class="noteref" refid="{@attr1}"><xsl:value-of select="$number"/></span>
     <span class="note" id="{@attr1}">
       [<xsl:value-of select="$number"/>. <xsl:apply-templates select="../../../Notes/FNote[ID/@attr1=$refid]/Para/ParaLine/*"/>]
     </span>
   </xsl:template>

   <xsl:template match="Char">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
<!--
Tab, HardSpace, SoftHyphen, HardHyphen, DiscHyphen, NoHyphen, Cent, Pound, Yen, EnDash,
EmDash, Dagger, DoubleDagger, Bullet, HardReturn, NumberSpace, ThinSpace, EnSpace, EmSpace.
* Mif Reference p. 123 *
-->
   <xsl:choose>
     <xsl:when test="@attr1='Tab'"><xsl:value-of select="'&#x0009;'"/></xsl:when>
     <xsl:when test="@attr1='HardSpace'"><xsl:value-of select="' '"/></xsl:when>
     <xsl:when test="@attr1='SoftHyphen'"/>
     <xsl:when test="@attr1='HardHyphen'"> - </xsl:when>
     <xsl:when test="@attr1='DiscHyphen'"/>
     <xsl:when test="@attr1='NoHyphen'"/>
     <xsl:when test="@attr1='Cent'"><xsl:value-of select="'&#x00A2;'"/></xsl:when>
     <xsl:when test="@attr1='Pound'"><xsl:value-of select="'&#x00A3;'"/></xsl:when>
     <xsl:when test="@attr1='Yen'"><xsl:value-of select="'&#x00A5;'"/></xsl:when>
     <xsl:when test="@attr1='EnDash' or @attr1='EmDash'"> - </xsl:when>
     <xsl:when test="@attr1='Dagger'"/>
     <xsl:when test="@attr1='DoubleDagger'"/>
     <xsl:when test="@attr1='Bullet'"><xsl:value-of select="'&#x2022;'"/></xsl:when>
     <xsl:when test="@attr1='HardReturn'"><br></br></xsl:when>
     <xsl:when test="@attr1='NumberSpace' or @attr1='ThinSpace' or @attr1='EnSpace' or @attr1='EmSpace'">
        <xsl:value-of select="' '"/>
      </xsl:when>
     <xsl:otherwise>
       <xsl:call-template name="warning">
         <xsl:with-param name="value" select="concat('Oförutsett element: ', @attr1)"/>
       </xsl:call-template>
     </xsl:otherwise>
   </xsl:choose>
   </xsl:template>
</xsl:stylesheet>

