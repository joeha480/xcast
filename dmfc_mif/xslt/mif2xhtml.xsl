<!--
*
* MIFXML to XHTML
* Version 2005-03-07 16:37
* Joel Håkansson, TPB
*
* Bilder hanteras inte
* viss text upprepas (varning finns i utfilen)
* Tabellhuvud och tabellfot kommer med, men saknar särskild uppmärkning
* Cellspan/Rowspan hanteras inte
* PgfNumStr kommer inte alltid med (p.g.a fulfix)
* Noter: Numreringen är fel. Texten kommer på plats, direkt efter referensen.
* (Markers saknas)
* Wrappade P-taggar, är inget problem vid export till Word, men kan innebära problem vid konvertering till DTBook
* Vissa p-taggar har style-attribut (Ej tillåtet i XHTML)
* 
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="/">
      <html>
         <head>
            <title />
         </head>
         <body>
            <p class="Version">Skapad med MIFXML to XHTML<br />
            Version 2005-03-07 16:37</p>
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
            <xsl:apply-templates select="document(concat(translate(substring-after(./FileName/@attr1, '&lt;c&gt;'), ' ','_'),'.mif.xml'),root)" mode="part" />
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
   <p class="debug" style="color: #FF0000; background-color: #A0A0A0">
      <xsl:value-of select="$value" />
   </p>
   </xsl:template>
   
   <xsl:template name="warning">
   <xsl:param name="value" />
   <xsl:param name="pn" />
   <p class="warning" style="color: #000000; background-color: #FF20FF">
      <xsl:value-of select="$value" />
   </p>
   </xsl:template>

   <xsl:template name="file">
      <xsl:for-each select="MIFXML/Page">
         <xsl:if test="./PageType/@attr1='BodyPage'">
            <xsl:variable name="pn" select="./PageNum/@attr1" />
            <xsl:for-each select="child::TextRect">
               <xsl:choose>
                  <xsl:when test="self::TextRect">
                     <xsl:variable name="TFID" select="./ID/@attr1" />
<!--
                     <xsl:variable name="TRNext" select="./TRNext/@attr1" />
                     <xsl:variable name="firstPara" select="../../TextFlow/Para/ParaLine/TextRectID[@attr1=$TFID]/../.." />
                     <xsl:apply-templates select="$firstPara[1]">-->
<!-- För varje Para som har TextRectID=$TFID -->
                     <xsl:variable name="firstID" select="generate-id(../../TextFlow/Para[ParaLine/TextRectID/@attr1=$TFID][1])"/>
                     <xsl:for-each select="../../TextFlow/Para[ParaLine/TextRectID/@attr1=$TFID]">
                     
<!-- Om det här är första stycket på en ny sida, så ge variablen pnOk rätt nummer -->
                     <xsl:variable name="pnOk">
                       <xsl:choose>
                         <xsl:when test="$firstID=generate-id(.)"><xsl:value-of select="$pn"/></xsl:when>
                         <xsl:otherwise/>
                       </xsl:choose>
                     </xsl:variable>

<!-- Det blir problem när ett stycke innehåller flera textrectId med olika värden eftersom 
     kontrollen sker på styckenivå. Detta är inte så vanligt, men kan förekomma. Just nu markeras detta endast för
     manuell kontroll. -->
<xsl:if test="count(./ParaLine/TextRectID)&gt;1">
      <xsl:call-template name="warning">
         <xsl:with-param name="value">VARNING! Text kan ha upprepats, kontrollera innehållet på föregående och efterföljande sida.</xsl:with-param>
      </xsl:call-template>
</xsl:if>

                     <xsl:apply-templates select=".">
                        <xsl:with-param name="pn" select="$pnOk" />
                     </xsl:apply-templates>
<!-- 
Välj alla följande Para (som inte själva har ett eget TextRectID) 
som har den här Para som (närmsta föregående granne med ett TextRectID) 
-->
                     <xsl:apply-templates 
                       select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                               (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())
                             ]"/>
  
                     </xsl:for-each>
                  </xsl:when>
                  <xsl:otherwise />
               </xsl:choose>
            </xsl:for-each>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>

<!-- Används troligtvis inte -->
   <xsl:template name="style">
   <xsl:param name="pn" />
   <xsl:attribute name="style">background-color: #c0c0c0; border-style: solid; border-width: 1px; border-color: #ff0000; margin: 1px;</xsl:attribute>
   </xsl:template>

<!-- välj alla barn till den här nodens förälders följande ParaLine-syskon som har den här fontnoden som sin närmaste föregående förälders syskonbarn-->
<!-- generate-id(parent::preceding-sibling::ParaLine::Font[last()]) = generate-id(current()) -->
<!-- parent::following-sibling::ParaLine::child::following-sibling[1] -->
   <xsl:template match="Font" mode="ft">
   <xsl:param name="pn" />
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
            </xsl:call-template>
         </span>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-Start">
            <xsl:with-param name="Current" select="$FontInfo" />
            <xsl:with-param name="pn" select="$pn" />
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-Final">
   <xsl:param name="pn" />
   <xsl:call-template name="getTextNodes">
      <xsl:with-param name="pn" select="$pn" />
   </xsl:call-template>
   </xsl:template>

   <xsl:template name="FontTests-03">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:choose>
      <xsl:when test="$Current/FWeight/@attr1='Bold' or $Current/FWeight/@attr1='SemiBold'">
         <strong>
            <xsl:call-template name="FontTests-Final">
               <xsl:with-param name="Current" select="$Current" />

               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </strong>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-Final">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-02">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:choose>
      <xsl:when test="$Current/FPosition/@attr1='FSubscript'">
         <sub>
            <xsl:call-template name="FontTests-03">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </sub>
      </xsl:when>

      <xsl:otherwise>
         <xsl:call-template name="FontTests-03">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-01">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:choose>
      <xsl:when test="$Current/FPosition/@attr1='FSuperscript'">
         <sup>
            <xsl:call-template name="FontTests-02">
               <xsl:with-param name="Current" select="$Current" />

               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </sup>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-02">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-Start">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:choose>
      <xsl:when test="$Current/FAngle/@attr1='Italic'">
         <em>
            <xsl:call-template name="FontTests-01">
               <xsl:with-param name="Current" select="$Current" />

               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </em>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-01">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="getTextNodes">
   <xsl:param name="pn" />
<!-- välj alla följande syskon som har den här fontnoden som närmaste föregående syskon -->
   <xsl:apply-templates select="following-sibling::*[generate-id(preceding-sibling::Font[1]) = generate-id(current())]">
      <xsl:with-param name="pn" select="$pn" />
   </xsl:apply-templates>
<!-- Dont ask, it works ;) -->
   <xsl:apply-templates select=" ../following-sibling::ParaLine/*[generate-id(((../preceding-sibling::ParaLine/Font)|(preceding-sibling::Font))[last()]) = generate-id(current()) ]">
      <xsl:with-param name="pn" select="$pn" />
   </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="Para">
   <xsl:param name="pn">0</xsl:param>
   <p class="{translate(PgfTag/@attr1, ' .&lt;&gt;+=', '')}">
<!-- behandla noder före första fontnoden -->
<!--  <span style="background-color: #ff80ff"> -->
         <xsl:apply-templates select="ParaLine/*[count(../preceding-sibling::ParaLine/Font)+count(preceding-sibling::Font)=0]">
            <xsl:with-param name="pn" select="$pn" />
         </xsl:apply-templates>
<!--  </span> -->
<!-- behandla alla fontnoder (och innehållet som följer fram till nästa fontnod)-->
      <xsl:apply-templates select="ParaLine/Font" mode="ft">
         <xsl:with-param name="pn" select="$pn" />
      </xsl:apply-templates>
   </p>
   </xsl:template>

   <xsl:template name="FormatPageNum">
   <xsl:param name="pn">0</xsl:param>
   <xsl:if test="$pn &gt; 0">
      <span id="p-{$pn}" class="pagenum-normal" style="background-color: #d0a040">
         <xsl:value-of select="$pn" />
      </span>
   </xsl:if>
   </xsl:template>
   
   <xsl:template match="TextRectID">
   <xsl:param name="pn" />
   <xsl:call-template name="FormatPageNum">
     <xsl:with-param name="pn" select="$pn"/>
   </xsl:call-template>
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
   <xsl:apply-templates select="/MIFXML/Tbls/Tbl[TblID/@attr1=$id]/TblTitle/TblTitleContent/Para"/>
   <table border="1">
      <xsl:apply-templates select="/MIFXML/Tbls/Tbl[TblID/@attr1=$id]/TblH/Row" mode="table"/>
      <xsl:apply-templates select="/MIFXML/Tbls/Tbl[TblID/@attr1=$id]/TblBody/Row" mode="table"/>
      <xsl:apply-templates select="/MIFXML/Tbls/Tbl[TblID/@attr1=$id]/TblF/Row" mode="table"/>
   </table>
   </xsl:template>

   <xsl:template match="AFrame">
   <!-- <xsl:param name="pn" /> -->
   <xsl:variable name="id" select="@attr1" />
   <xsl:for-each select="/MIFXML/AFrames/Frame[ID/@attr1=$id]/*">
      <xsl:choose>
         <xsl:when test="self::TextRect">
            <xsl:variable name="id2" select="./ID/@attr1" />
            <xsl:variable name="fp2" select="/MIFXML/TextFlow/Para[ParaLine/TextRectID/@attr1=$id2]" />
            <xsl:apply-templates select="$fp2[1]">
               <xsl:with-param name="TFID" select="$id2" />
               <!-- <xsl:with-param name="pn" select="$pn" /> -->
               <xsl:with-param name="TRNext" select="0" />
            </xsl:apply-templates>
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
   <xsl:value-of select="." />
   </xsl:template>

   <xsl:template match="String">
   <xsl:param name="pn" />
   <xsl:if test="@attr1 = ../../ParaLine[1]/String[1]/@attr1">
      <xsl:value-of select="../../PgfNumString/@attr1" />
   </xsl:if>
   <xsl:value-of select="@attr1" />
   </xsl:template>
   
   <xsl:template match="FNote">
     <xsl:variable name="refid" select="@attr1"/>
     <span class="noteref" refid="{@attr1}"><sup><xsl:value-of select="$refid"/></sup></span>
     <span class="note" id="{@attr1}">
       [<xsl:value-of select="$refid"/>. <xsl:apply-templates select="../../../Notes/FNote[ID/@attr1=$refid]/Para/ParaLine/*"/>]
     </span>
   </xsl:template>

   <xsl:template match="Char">
   <xsl:param name="pn" />
   <xsl:choose>
      <xsl:when test="@attr1='EnDash' or @attr1='EmDash'"> - </xsl:when>
      <xsl:when test="@attr1='SoftHyphen' or @attr1='DiscHypen'" />
      <xsl:when test="@attr1='HardReturn'">
         <br>
         </br>
      </xsl:when>
      <xsl:when test="@attr1='HardSpace' or @attr1='ThinSpace' or @attr1='EnSpace'"><xsl:value-of select="' '"/></xsl:when>
      <xsl:when test="@attr1='Tab'"><xsl:value-of select="'&#0009;'"/></xsl:when>
      <xsl:otherwise><!-- Något icke-hanterat -->
<!--
Tab, HardSpace, SoftHyphen, HardHyphen, DiscHyphen, NoHyphen, Cent, Pound, Yen, EnDash,
EmDash, Dagger, DoubleDagger, Bullet, HardReturn, NumberSpace, ThinSpace, EnSpace, EmSpace.
* Mif Reference p. 123 *
-->
        <span class="Debug">!--<xsl:value-of select="@attr1" />--!</span>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>
</xsl:stylesheet>

