from storage import Professor

import re

class TrieNode:
    def __init__(self):
        self.Next = dict()
        self.values = set()
        self.strings = set()

class Trie:
    def __init__(self):
        self.root = TrieNode()

    def add(self, string, value):
        node = self.root
        i = 0
        while i < len(string):
            if not (string[i] in node.Next):
                node.Next[string[i]] = TrieNode()
            node = node.Next[string[i]]
            i = i + 1
        if len(node.values) == 0:
            for i in range(1, len(string)):
                self.__add_suffix__(string, i)
        node.strings.add(string)
        node.values.add(value)

    def __add_suffix__(self, string, i):
        node = self.root
        while i < len(string):
            if not (string[i] in node.Next):
                node.Next[string[i]] = TrieNode()
            node = node.Next[string[i]]
            i = i + 1
        node.strings.add(string)

    def get(self, string):
        node = self.root
        index = 0
        while index < len(string):
            if not (string[index] in node.Next):
                return []
            node = node.Next[string[index]]
            index = index + 1
        return node.values

    def searchSubstring(self, substring):
        node = self.root
        index = 0
        while index < len(substring):
            if not (substring[index] in node.Next):
                return []
            node = node.Next[substring[index]]
            index = index + 1
        return self.__traversal_strings__(node, set())

    def __traversal_strings__(self, node, result):
        for string in node.strings:
            result.add(string)
        for n in node.Next.values():
            self.__traversal_strings__(n, result)
        return result

    def remove(self, string, value):
        node = self.root
        index = 0
        while index < len(string):
            if not (string[index] in node.Next):
                return
            node = node.Next[string[index]]
            index = index + 1
        if value in node.values:
            node.values.remove(value)
        if len(node.values) == 0:
            for i in range(0, len(string)):
                self.__remove_suffix__(string, self.root, i)

    def __remove_suffix__(self, string, node, i):
        if node is None:
            return None
        if i == len(string):
            if string in node.strings:
                node.strings.remove(string)
        elif string[i] in node.Next:
            node.Next[string[i]] = self.__remove_suffix__(string, node.Next[string[i]], i+1)
            if node.Next[string[i]] is None:
                del(node.Next[string[i]])
        if len(node.Next) == 0 and len(node.strings) == 0 and len(node.values) == 0:
            return None
        else:
            return node


# Format string for easier search
def formatSearchContent(string):
    string = string.lower()
    segs = re.findall(r'[0-9a-zA-Z]+', string)
    nonEnglish = re.findall(r'[^\x00-\x7f]+', string)
    return segs + nonEnglish


# Build the trie.
def buildTrie():
    trie = Trie()
    for professor in Professor.get_all_professors():
        for str in formatSearchContent(professor.name):
            trie.add(str, professor.get_id())
        for str in formatSearchContent(professor.title):
            trie.add(str, professor.get_id())
        if professor.special_title:
            for str in formatSearchContent(professor.special_title):
                trie.add(str, professor.get_id())
        if professor.introduction:
            for str in formatSearchContent(professor.introduction):
                trie.add(str, professor.get_id())
        for research_area in professor.research_areas:
            for str in formatSearchContent(research_area):
                trie.add(str, professor.get_id())
        for research_interest in professor.research_interests:
            for str in formatSearchContent(research_interest):
                trie.add(str, professor.get_id())
        for research_group in professor.research_groups:
            for str in formatSearchContent(research_group):
                trie.add(str, professor.get_id())
        if professor.office:
            for str in formatSearchContent(professor.office):
                trie.add(str, professor.get_id())
        if professor.phone:
            for str in formatSearchContent(professor.phone):
                trie.add(str, professor.get_id())
        if professor.email:
            for str in formatSearchContent("".join(re.findall(r'(.+)@', professor.email))):
                trie.add(str, professor.get_id())
    return trie

# Build word occurence dict
def buildOccurrenceDict():
    ret = dict()
    for professor in Professor.get_all_professors():
        prof_dict = dict()
        for str in formatSearchContent(professor.name):
            if str in prof_dict:
                prof_dict[str] = prof_dict[str] + 1
            else:
                prof_dict[str] = 0
        for str in formatSearchContent(professor.title):
            if str in prof_dict:
                prof_dict[str] = prof_dict[str] + 1
            else:
                prof_dict[str] = 0
        if professor.special_title:
            for str in formatSearchContent(professor.special_title):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        if professor.introduction:
            for str in formatSearchContent(professor.introduction):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        for research_area in professor.research_areas:
            for str in formatSearchContent(research_area):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        for research_interest in professor.research_interests:
            for str in formatSearchContent(research_interest):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        for research_group in professor.research_groups:
            for str in formatSearchContent(research_group):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        if professor.office:
            for str in formatSearchContent(professor.office):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        if professor.phone:
            for str in formatSearchContent(professor.phone):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        if professor.email:
            for str in formatSearchContent("".join(re.findall(r'(.+)@', professor.email))):
                if str in prof_dict:
                    prof_dict[str] = prof_dict[str] + 1
                else:
                    prof_dict[str] = 0
        ret[professor.get_id()] = prof_dict
    return ret


PROFESSOR_TRIE = buildTrie()
OCCURRENCE_DICT = buildOccurrenceDict()


# Search for results of a query, return professors
def search_professors(query_words):
    query_words = formatSearchContent(query_words)
    pid = []
    relevence = {}
    trie = PROFESSOR_TRIE
    for word in query_words:
        pids_of_word = set()
        for string in trie.searchSubstring(word):
            pids_of_string = trie.get(string)
            for prof_id in pids_of_string:
                if not(prof_id in relevence):
                    relevence[prof_id] = 0.0
                relevence[prof_id] = relevence[prof_id] + float(len(word))/len(string) * OCCURRENCE_DICT[prof_id][string]
            pids_of_word = pids_of_word.union(pids_of_string)
        pid.append(pids_of_word)
    ret = [Professor.get_professor(prof_id) for prof_id in reduce(lambda x, y: x.intersection(y), pid)]
    return sorted(ret, key=lambda x: -relevence[x.key.id()])


# Get search suggestions from query words
def search_suggestion(query_words, max_num):
    query_words = formatSearchContent(query_words)
    suggestions = []
    trie = PROFESSOR_TRIE
    for word in query_words:
        suggestion = []
        for string in trie.searchSubstring(word):
            suggestion.append(string)
        suggestions.append(suggestion)
    result = set()
    combineSuggestions(trie, '', set(), suggestions, 0, result, max_num)
    return sorted([i for i in result])


# Get all combinations of suggestions
def combineSuggestions(trie, cur_string, cur_str_set, suggestions, index, result, max_num):
    if len(result) >= max_num:
        return
    if index == len(suggestions):
        m = [trie.get(string) for string in cur_str_set]
        id_set = reduce(lambda x, y: x.intersection(y), [trie.get(string) for string in cur_str_set])
        if len(id_set) > 0:
            result.add(cur_string)
        return
    for suggest in suggestions[index]:
        cur = cur_string
        dup = True
        if not(suggest in cur_str_set):
            cur_str_set.add(suggest)
            cur = cur_string + (' ' if len(cur_string) > 0 else '') + suggest
            dup = False
        combineSuggestions(trie, cur, cur_str_set, suggestions, index+1, result, max_num)
        if not dup:
            cur_str_set.remove(suggest)