from bs4 import BeautifulSoup
import urllib2
import json
import re


# Get html string from an url
def get_html_str(url):
    html = ""
    for line in urllib2.urlopen(url):
        html += line
    return html


# Get Faculty Info from a faculty web page
def get_faculty_info(url):
    try:
        soup = BeautifulSoup(get_html_str(url), 'html.parser')
        info = dict()
        figure = soup.find('figure')
        info['image'] = figure.find('a')['href'] if figure else None
        info['name'] = soup.find('div', class_='faculty-profile-title').find('h1').string
        info['title'] = soup.find('div', class_='field-name-field-faculty-position').find('div', class_='field-item even').string
        special_title = soup.find('div', class_='field-name-field-special-title')
        info['special title'] = special_title.find('div', class_='field-item even').string if special_title else None
        introduction = soup.find('div', class_='faculty-profile-content').find('p')
        info['introduction'] = introduction.string if introduction else None
        research_areas = soup.find('section', class_='field-name-field-related-research-areas')
        info['research areas'] = [tag.string for tag in research_areas.find_all('a')] if research_areas else []
        research_interests = soup.find('section', class_='field-name-field-research-interests')
        info['research interests'] = [tag.string for tag in research_interests.find_all('div', class_='field-item')] if research_interests else []
        research_groups = soup.find('section', class_='field-name-field-research-groups')
        info['research groups'] = [tag.string for tag in research_groups.find_all('a')] if research_groups else []
        office = soup.find('section', class_='field-name-field-office')
        info['office'] = office.find('div', class_='field-item').string if office else None
        phone = soup.find('section', class_='field-name-field-phone')
        info['phone'] = phone.find('div', class_='field-item').string if phone else None
        email = soup.find('section', class_='field-name-field-email')
        info['email'] = re.findall(r'.*mailto:(.*)', email.find('a')['href'])[0] if email else None
        personal_website = soup.find('div', class_='field-name-field-website-personal')
        info['personal website'] = personal_website.find('a')['href'] if personal_website else None
    except Exception as e:
        print "Error occurs when parse url "+url
        raise e
    return info


soup = BeautifulSoup(get_html_str("http://www.ece.utexas.edu/people/faculty"), 'html.parser')
URL_Faculties = [tag.find("a")['href'] for tag in soup.find_all("div", class_="views-field-field-last-name")]
professors = [get_faculty_info(url) for url in URL_Faculties]
output = file('output.json', 'w')
json.dump(professors, output)